# Share draft — nothing.community / Reddit / X

Copy-paste and adjust links after you create the GitHub repo.

---

**Title:** Glyph Stock Ticker — TSLA / BTC / NVDA on Phone (3) matrix

**Body:**

I built a Glyph Toy for the Nothing Phone (3) that shows live prices on the back matrix:

- Default: **TSLA**
- **Long-press** Glyph Button: switch to **BTC**, then **NVDA**, then back to TSLA
- Lines: symbol / integer price / integer daily % change
- Refreshes every 5 min + on AOD

No API key — uses Yahoo Finance chart endpoint. Install APK or build from source.

**GitHub:** https://github.com/literato1987/glyph-stock-ticker  
**APK (v1.4.0):** https://github.com/literato1987/glyph-stock-ticker/releases/download/v1.4.0/glyph-stock-ticker-v1.4.0.apk

**Requires:** Phone (3), Nothing OS with Glyph Toy support, internet.

**Activate:** install app → open → "Activate Glyph Toy" → drag toy to Active in Settings → Glyph Interface.

Not financial advice — hobby project. Feedback welcome, especially if BTC price is too wide on the matrix (may add `97k` format).

Built with the official [Glyph Matrix Developer Kit](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit).

**Published on nothing.community:** https://nothing.community/d/40994-community-glyph-matrix-toys-collection (post #291100)

---

## GitHub repo setup (one-time)

```bash
cd glyph-tesla-stock
# Remove template remote (points to Nothing's example repo)
git remote remove origin
# Create empty repo on GitHub, then:
git remote add origin git@github.com:YOUR_USER/glyph-stock-ticker.git
git add README.md LICENSE docs/ releases/ app/src/
git commit -m "Release v1.4.0 — stock ticker Glyph Toy for Phone (3)"
git push -u origin main
```

Then in GitHub: **Settings → Topics** → add `glyph-toy`, `glyph-matrix`, `nothing-phone`, `nothing-phone-3`, `kotlin`, `android`.

Optional: create a GitHub Release tagged `v1.4.0` and attach the APK.