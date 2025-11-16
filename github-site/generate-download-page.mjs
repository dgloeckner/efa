#!/usr/bin/env node
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// ---- Configuration ----
const OWNER = process.env.GITHUB_REPOSITORY_OWNER || "dgloeckner";
const REPO = process.env.GITHUB_REPOSITORY?.split("/")[1] || "efa";
const MAX_RELEASES = Number(process.env.MAX_RELEASES || "10");
const TOKEN = process.env.GITHUB_TOKEN; // provided by Actions
const API_URL = `https://api.github.com/repos/${OWNER}/${REPO}/releases`;

const TEMPLATE_PATH = path.join(__dirname, "template.html");
const OUTPUT_DIR = path.join(__dirname, "..", "dist");
const OUTPUT_FILE = path.join(OUTPUT_DIR, "index.html");

// Very minimal Markdown → HTML (headings, paragraphs, bullets, links)
// Enough for release notes without adding a full markdown library.
function simpleMarkdownToHtml(markdown) {
    if (!markdown) return "<p>No release notes provided.</p>";

    let html = markdown.trim();

    // Escape basic HTML
    html = html
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");

    // headings (## and ###)
    html = html.replace(/^### (.*)$/gm, "<p><strong>$1</strong></p>");
    html = html.replace(/^## (.*)$/gm, "<p><strong>$1</strong></p>");

    // bullet lists
    html = html.replace(/^\s*[-*] (.*)$/gm, "<ul><li>$1</li></ul>");
    // collapse nested <ul> duplication from regex (quick cleanup)
    html = html.replace(/<\/ul>\s*<ul>/g, "");

    // links [text](url)
    html = html.replace(
        /\[([^\]]+)\]\((https?:\/\/[^\s)]+)\)/g,
        '<a href="$2" target="_blank" rel="noreferrer">$1</a>',
    );

    // paragraphs: split by double newline
    html = html
        .split(/\n{2,}/)
        .map((block) => {
            if (block.startsWith("<ul>") || block.startsWith("<p>")) return block;
            return `<p>${block.replace(/\n/g, "<br>")}</p>`;
        })
        .join("\n");

    return html;
}

function formatDate(iso) {
    if (!iso) return "";
    const d = new Date(iso);
    return d.toLocaleDateString("en-GB", {
        year: "numeric",
        month: "short",
        day: "2-digit",
    });
}

function humanFileSize(bytes) {
    if (!bytes && bytes !== 0) return "";
    const thresh = 1024;
    if (Math.abs(bytes) < thresh) return `${bytes} B`;
    const units = ["KB", "MB", "GB", "TB"];
    let u = -1;
    do {
        bytes /= thresh;
        ++u;
    } while (Math.abs(bytes) >= thresh && u < units.length - 1);
    return `${bytes.toFixed(1)} ${units[u]}`;
}

async function fetchReleases() {
    const res = await fetch(`${API_URL}?per_page=${MAX_RELEASES}`, {
        headers: {
            Accept: "application/vnd.github+json",
            "User-Agent": "release-page-generator",
            ...(TOKEN ? { Authorization: `Bearer ${TOKEN}` } : {}),
        },
    });
    if (!res.ok) {
        const text = await res.text();
        throw new Error(`GitHub API error ${res.status}: ${text}`);
    }
    return res.json();
}

function renderRelease(release) {
    const tag = release.tag_name;
    const title = release.name || tag;
    const date = formatDate(release.published_at || release.created_at);
    const bodyHtml = simpleMarkdownToHtml(release.body || "");
    const assets = release.assets || [];

    // Only show ZIP files and their corresponding checksum links (e.g., .sha1, .sha256, .sha512)
    const zipAssets = (assets || []).filter(a => a.name && a.name.toLowerCase().endsWith(".zip"));

    const assetButtons =
        zipAssets.length === 0
            ? `<span class="meta">No ZIP assets attached. View on <a href="${release.html_url}" target="_blank" rel="noreferrer">GitHub</a>.</span>`
            : zipAssets
                .map((zip) => {
                    const lowerZipName = zip.name.toLowerCase();
                    const checksumAssets = (assets || []).filter(a => {
                        const n = (a.name || "").toLowerCase();
                        if (n === lowerZipName) return false; // it's the zip itself
                        const isSha = /\.sha(\d+)?(\.txt)?$/.test(n) || /sha(\d+)?sum/.test(n);
                        if (!isSha) return false;
                        // Associate checksum with ZIP by name prefix or including the zip name
                        return n.startsWith(lowerZipName) || n.includes(lowerZipName + ".");
                    });

                    const checksumsHtml = checksumAssets.length
                        ? `<span class="meta">Checksums: ` + checksumAssets
                            .map(cs => `<a href="${cs.browser_download_url}" download>${cs.name.replace(zip.name, "").replace(/^([._-])/, "").replace(/^\./, "") || cs.name}</a>`)
                            .join(" · ") + `</span>`
                        : "";

                    return `
<div class="asset-zip">
  <a class="asset-button" href="${zip.browser_download_url}" download>
    <span class="asset-kind">📦</span>
    <span class="asset-name">${zip.name}</span>
    <span class="meta">ZIP · ${humanFileSize(zip.size)}</span>
  </a>
  ${checksumsHtml}
</div>`;
                })
                .join("\n");

    return `
<section class="release-card">
  <div class="release-header">
    <div>
      <div class="release-title">${title}</div>
    </div>
    <div class="release-date">${date}</div>
  </div>
  <div class="release-body">
    ${bodyHtml}
  </div>
  <div class="assets">
    ${assetButtons}
  </div>
</section>`;
}

async function main() {
    console.log(`Generating download page for ${OWNER}/${REPO}...`);
    const template = fs.readFileSync(TEMPLATE_PATH, "utf8");
    const releases = await fetchReleases();

    if (!Array.isArray(releases) || releases.length === 0) {
        throw new Error("No releases found.");
    }

    const selected = releases.slice(0, MAX_RELEASES);
    const releasesHtml = selected.map(renderRelease).join("\n\n");

    const latest = selected[0];
    const latestTag = latest.tag_name;
    const releaseCount = selected.length;
    const generatedAt = new Date().toISOString();

    let html = template.replace("<!--RELEASES-->", releasesHtml);
    const repoUrl = `https://github.com/${OWNER}/${REPO}`;

    html = html
        .replace('id="latest-version">–', `id="latest-version">${latestTag}`)
        .replace('id="release-count">–', `id="release-count">${releaseCount}`)
        .replace('id="repo-link" href="#"', `id="repo-link" href="${repoUrl}"`)
        .replace(
            'id="repo-link-footer" href="#"',
            `id="repo-link-footer" href="${repoUrl}"`,
        )
        .replace('id="generated-at">–', `id="generated-at">${generatedAt}`);

    fs.mkdirSync(OUTPUT_DIR, { recursive: true });
    fs.writeFileSync(OUTPUT_FILE, html, "utf8");
    console.log(`Wrote ${OUTPUT_FILE}`);
}

main().catch((err) => {
    console.error(err);
    process.exit(1);
});
