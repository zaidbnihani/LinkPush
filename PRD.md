# PRD: Link Push — Marketing Landing Page

**Document type:** Product Requirements Document (PRD)
**Product:** Link Push (Android app)
**Deliverable:** Professional marketing website / landing page
**Owner:** ZaidHB
**Repo reference:** https://github.com/zaidbnihani/LinkPush
**Version:** 1.0
**Date:** August 4, 2026

---

## 1. Overview

### 1.1 Product Summary
Link Push is an Android application that lets users share links (and related content) instantly between devices over a local Wi-Fi network, with automatic device discovery — no internet connection, no third-party server, no account required.

### 1.2 Purpose of This Document
Define requirements for a single-page (or lightly multi-section) marketing website that:
- Explains what Link Push does and why it's useful
- Builds trust and brand awareness for the ZaidHB app
- Drives installs (Google Play primary CTA)
- Can also serve as a landing page for paid/organic campaigns

### 1.3 Goals
| Goal | Success Signal |
|---|---|
| Communicate value in <10 seconds | Clear hero headline + subhead understood without scrolling |
| Drive app installs | High CTR on primary "Download" CTA |
| Establish credibility | Clean design, no broken links, real feature screenshots |
| Support paid traffic | Fast load time, mobile-first, works as ad landing page |

### 1.4 Non-Goals
- No user authentication / backend required for the site itself
- No blog or documentation hub (out of scope for v1)
- No multi-language site in v1 (Arabic RTL primary; English optional in v2 — to be confirmed)

---

## 2. Target Audience

| Segment | Description |
|---|---|
| Primary | Android users who frequently share links/files between their own devices or with nearby friends/colleagues (privacy-conscious, dislike relying on cloud services or messaging apps) |
| Secondary | Users in low/no-internet environments who need local, offline sharing |
| Tertiary | Tech-savvy Arabic-speaking audience (ZaidHB's existing community/followers) |

---

## 3. Key Value Propositions (to feature prominently)

1. **No internet needed** — works fully over local Wi-Fi
2. **Automatic device discovery** — no manual pairing/QR codes required
3. **Instant transfer** — fast, direct device-to-device
4. **Privacy-first** — nothing passes through external servers
5. **Free / lightweight** — no bloat, no unnecessary permissions

*(Note: exact feature list should be verified against the current app README/feature set before final copywriting — the repo's public description was the only source available at PRD time.)*

---

## 4. Site Structure & Page Requirements

### 4.1 Hero Section
- App name + logo (to be designed)
- One-line value proposition (headline)
- Supporting subheadline (1–2 sentences)
- Primary CTA: **"Download on Google Play"** button
- Secondary CTA: "See how it works" (scroll anchor)
- Hero visual: phone mockup showing the app UI or a device-discovery animation

### 4.2 "How It Works" Section
- 3-step visual explainer (e.g., Open app → Devices auto-detected → Tap to send)
- Icons or simple illustrations per step
- Optional short looping demo GIF/video

### 4.3 Features Section
- Grid of 4–6 feature cards, each with icon + short title + 1-line description
- Suggested cards: No Internet Required, Auto Device Discovery, Fast Local Transfer, Privacy by Design, Simple UI, Free to Use

### 4.4 Screenshots / Visual Proof Section
- Carousel or grid of real app screenshots (placeholder frames until assets provided)
- Optional device frame mockups (Android phone frame)

### 4.5 Why Link Push / Comparison Section (optional but recommended)
- Short comparison vs. alternatives (Bluetooth sharing, cloud-based link sharing, messaging apps) — highlighting speed/privacy/no-internet advantage
- Table or 3-column comparison format

### 4.6 Testimonials / Social Proof (optional, v2)
- Placeholder for future user reviews or Play Store rating badge

### 4.7 Final CTA Section
- Repeat primary download CTA
- Google Play badge (official asset)
- Optional: QR code linking directly to Play Store listing

### 4.8 Footer
- Links: Privacy Policy, Contact, GitHub repo (optional), ZaidHB brand link/socials
- Copyright line

---

## 5. Functional Requirements

| ID | Requirement | Priority |
|---|---|---|
| FR-1 | Site must be fully responsive (mobile-first, since target users are mobile/Android-focused) | Must |
| FR-2 | Primary CTA button links directly to the Google Play Store listing | Must |
| FR-3 | Page must load in under 2 seconds on 4G | Must |
| FR-4 | RTL (Arabic) layout support as default language | Must |
| FR-5 | Smooth scroll navigation between sections | Should |
| FR-6 | SEO meta tags (title, description, Open Graph, Twitter Card) | Must |
| FR-7 | Favicon and app icon consistent with brand | Must |
| FR-8 | Basic analytics hook (e.g., button click tracking) placeholder | Should |
| FR-9 | Accessible color contrast and alt text on images | Should |
| FR-10 | English version toggle (future-proofed, not required v1) | Could |

---

## 6. Non-Functional Requirements

- **Performance:** Lighthouse score 90+ on mobile (Performance, Accessibility, SEO)
- **Hosting:** Static site — deployable on any static host (Vercel/Netlify/GitHub Pages) or as a single HTML file
- **Browser support:** Latest 2 versions of Chrome, Safari, Firefox, Edge
- **No backend/database required**

---

## 7. Design Requirements

Since no existing brand assets were provided, design will be created from scratch with these constraints:
- Modern, clean, tech-forward aesthetic (not a generic template look)
- Color palette to reflect connectivity/speed themes (e.g., deep blue/teal + energetic accent color) — final palette to be proposed in design phase
- Typography: clear, modern sans-serif; RTL-friendly Arabic web font
- Iconography: outline or duotone icon set matching feature list
- Dark mode: optional nice-to-have

---

## 8. Content Requirements (Copy To Be Finalized)

| Section | Content Needed |
|---|---|
| Hero headline | Short, benefit-driven (draft to be proposed) |
| Feature descriptions | 4–6 short blurbs |
| How-it-works steps | 3 short steps |
| Screenshots | Real app screenshots (not yet provided — placeholders will be used) |
| App store link | Actual Google Play URL (not yet provided — placeholder used) |
| Legal | Privacy Policy link/page (not yet provided) |

---

## 9. Open Questions / Items Needing Confirmation

1. Is the app currently live on Google Play, and what is the exact store URL?
2. Should the site be Arabic-only for v1, or bilingual (AR/EN) from launch?
3. Are there existing brand colors/logo from other ZaidHB products (Tatbiqati, etc.) that Link Push should align with, or is this a fully independent brand identity?
4. Should the site include a link back to the GitHub repo (open-source signal) or keep it purely consumer-facing?
5. Any specific competitor apps to differentiate against in messaging?

---

## 10. Deliverables

1. Single-page responsive HTML/CSS/JS landing page (or React, per implementation preference)
2. All copy in Arabic (RTL), structured per Section 4
3. Placeholder sections clearly marked for: screenshots, Play Store link, Privacy Policy
4. SEO-ready meta tags and Open Graph image

---

## 11. Next Steps

1. Confirm answers to Open Questions (Section 9)
2. Design direction sign-off (color palette, typography, layout mockup)
3. Build landing page (HTML/CSS/JS)
4. Review against real screenshots/assets once provided
5. Deploy to hosting and connect domain (if applicable)
