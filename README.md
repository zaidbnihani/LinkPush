# Product Requirements Document (PRD)
## QuranTune — Promotional Website

**Document Owner:** Zaid (ZaidHB)
**Product:** QuranTune Android App
**Document Type:** Multi-page marketing/promotional website
**Version:** 1.0
**Status:** Draft for development

---

## 1. Executive Summary

QuranTune is a modern Quran audio player Android application that goes beyond simple playback. It introduces cross-device synchronization via MQTT, offline-first architecture, and a frictionless QR-based device linking system, wrapped in a clean listening experience. This document specifies the requirements for a professional, multi-page marketing website whose primary conversion goal is driving direct app downloads (APK / Google Play / Tatbiqati distribution).

The website must communicate trust, technical sophistication, and spiritual respect for the subject matter simultaneously — a balance between an Islamic app and a polished tech product.

---

## 2. Goals & Objectives

### 2.1 Primary Goal
Maximize direct app downloads through a clear, frictionless conversion path repeated on every page (sticky/persistent CTA).

### 2.2 Secondary Goals
- Build credibility and trust (important for religious content apps — users are sensitive about Quran text/audio accuracy and privacy).
- Clearly differentiate QuranTune from generic Quran apps by foregrounding the MQTT sync and QR linking features, which are uncommon in this app category.
- Provide enough technical/feature depth to satisfy power users, while remaining approachable for average users (reciters, everyday listeners, elderly users).
- Serve as a durable landing surface that can be linked from YouTube videos, social media (ZaidHB Official / ZaidHB Market & Crypto), and Tatbiqati.

### 2.3 Non-Goals
- No e-commerce, no payment flow (app is free / not monetized on this site).
- No user accounts or backend on the website itself (fully static/marketing site).
- No in-browser Quran playback (site promotes the app; it does not replace it).

---

## 3. Target Audience

| Segment | Description | Priority |
|---|---|---|
| Daily Quran listeners | Users who want a reliable, distraction-free recitation player | High |
| Multi-device users | Users who own a phone + tablet + car system and want continuity | High |
| Arabic-speaking tech-savvy users | Familiar with ZaidHB's other apps/content, early adopters | Medium |
| Elderly/less technical users | Need simplicity, large touch targets, clear instructions | Medium |
| Reciters/Hifz students | Need offline reliability, repeat/loop features, precise navigation | Medium |

**Primary language of site:** English (per current scope). Arabic (RTL) localization should be architected for but not required in v1, given Zaid's other properties are Arabic-first — this site is the English-facing counterpart.

---

## 4. Site Architecture (Multi-Page)

```
/                      → Home (Hero + feature overview + primary CTA)
/features              → Deep-dive feature breakdown
/how-it-works           → MQTT sync + QR linking explained step-by-step
/download               → Download hub (Google Play / Tatbiqati / direct APK)
/screenshots (or /gallery) → Visual tour of the app
/faq                    → Frequently asked questions
/privacy                → Privacy Policy (mandatory for Play Store listing)
/about                  → About ZaidHB / the developer story
/contact (optional)     → Support / contact channel
```

A persistent top navigation bar and footer link all pages together. A sticky "Download Now" button/header CTA persists across every page (not just Home).

---

## 5. Core Feature Set (from app, to be marketed)

### 5.1 MQTT-Based Multi-Device Sync
- Real-time synchronization of playback position, current surah/reciter, and playlists across multiple devices owned by the same user.
- Powered by a lightweight MQTT broker connection — enables near-instant state sync without heavy polling or battery drain.
- Use case to market: "Start listening on your phone during your commute, resume exactly where you left off on your tablet at home."

### 5.2 QR Code Device Linking
- Pairing a new device is done by scanning a QR code shown on an already-linked device — no manual account creation friction, no typing long codes.
- Emphasize speed and simplicity: "Link a new device in under 10 seconds."

### 5.3 Offline Download & Playback
- Full offline-first design: download complete surahs or reciters for offline listening.
- No dependency on constant connectivity for core listening experience (important for users with limited data plans — a common concern in Jordan/MENA region).

### 5.4 Local Audio Library
- A dedicated local audio library management feature (recently added per the July 2026 audit/fix package) allowing users to organize and access downloaded content directly from local storage, independent of network conditions.

### 5.5 Reliable Playback Engine
- Recent engineering work (July 2026 audit) resolved YouTube-audio-source sync issues and MQTT integration bugs, and introduced a yt-dlp–based audio server for more robust streaming where applicable.
- Market this indirectly as "actively maintained, continuously improved" rather than exposing internal bug-fix details.

### 5.6 Clean, Distraction-Free UI
- (Assumption — standard for this app category) Minimalist player interface, night mode/dark mode for late-night or Tahajjud listening, adjustable playback speed, sleep timer, repeat/loop for memorization (Hifz) use cases.

> **Note on assumptions:** Sections 5.5–5.6 include reasonable inferences based on QuranTune's known engineering history and standard practices for Quran audio apps. These should be verified against the actual current app feature list before publishing final copy.

---

## 6. Page-by-Page Requirements

### 6.1 Home (`/`)
- **Hero section:** App name, one-line value proposition (e.g., "Your Quran, Perfectly in Sync — Across Every Device"), hero visual (phone mockup), primary CTA button ("Download Now").
- **Trust bar:** small row of trust signals (e.g., "100% Offline Capable," "No Ads," "Privacy-Respecting," rating badge placeholder).
- **Feature highlights (3–4 cards):** MQTT Sync, QR Linking, Offline Downloads, Local Library — icon + 1-2 sentence description each, linking to `/features` for depth.
- **"How it Works" teaser:** 3-step visual summary linking to full `/how-it-works` page.
- **Screenshot carousel/preview** linking to `/screenshots`.
- **Secondary CTA / footer download block** before the footer.

### 6.2 Features (`/features`)
- Full breakdown of every feature in Section 5, each with its own sub-section: heading, description, supporting screenshot/illustration, and (where relevant) a short "why it matters" note.
- Comparison framing encouraged: implicitly contrast with typical single-device Quran apps (without naming competitors).

### 6.3 How It Works (`/how-it-works`)
- Step-by-step visual walkthrough of:
  1. Install & open the app.
  2. Link additional devices via QR scan.
  3. Download surahs/reciters for offline use.
  4. Sync automatically across devices via MQTT as you listen.
- Consider simple numbered diagram or annotated screenshots.

### 6.4 Download (`/download`)
- Primary hub for all download channels: Google Play (if applicable), Tatbiqati platform link, direct APK link.
- Device/OS requirements (minimum Android version).
- File size, version number, last updated date (dynamic-ready placeholders).
- Basic install instructions for sideloaded APK (enable unknown sources) if direct APK is offered — important since Tatbiqati is Zaid's own distribution channel outside Play Store.

### 6.5 Screenshots / Gallery (`/screenshots`)
- Grid or carousel of real app screenshots: home/player screen, device linking (QR) screen, offline downloads manager, local library, settings/dark mode.
- Placeholder image slots clearly marked until real assets are supplied.

### 6.6 FAQ (`/faq`)
Suggested starter questions (to be refined with real user questions if available):
- Is QuranTune free?
- Does it work without internet?
- How many devices can I link?
- Is my data private / is anything uploaded to a server?
- Which reciters/audio sources are supported?
- How do I unlink a device?
- Is the app available outside Android?

### 6.7 Privacy Policy (`/privacy`)
- **Mandatory** for Google Play Store listing compliance regardless of marketing site scope.
- Must state clearly: what data is collected (if any), MQTT connection data handling, whether audio download data is stored locally only, third-party services used (e.g., yt-dlp/audio server, Firebase if used elsewhere in Zaid's stack), and contact for privacy inquiries.
- **Flag for Zaid:** this section needs real, accurate answers before publishing — it is a legal/compliance page, not just marketing copy, and should not be filled with placeholder assumptions in the final live version.

### 6.8 About (`/about`)
- Short brand story: built by ZaidHB, an independent Android/web developer, as part of a broader suite of apps distributed via Tatbiqati.
- Optional links to ZaidHB's other properties/social channels.

### 6.9 Contact (optional, `/contact`)
- Simple support channel (email or social handle) for bug reports/feedback — no live form/backend required for v1; can be a mailto: link or link to social account.

---

## 7. Design Direction

- **Tone:** Calm, respectful, modern — avoid overly flashy/gamified visuals given the religious subject matter, but still feel like a contemporary tech product (not dated or purely utilitarian).
- **Color palette suggestion:** Deep teal/emerald or navy as primary (associated with Islamic aesthetics without being cliché), warm neutral background, gold/amber as a sparing accent for CTAs.
- **Typography:** Clean modern sans-serif for UI text; optional elegant Arabic-inspired display font for the hero headline only (used sparingly).
- **Imagery:** Real device mockups showing the app UI > generic stock photography. Avoid depicting people; favor abstract patterns (geometric Islamic-art-inspired motifs) as decorative accents.
- **Dark mode:** Since the app itself likely has a night mode, the website should support (or default to) a dark theme option, reinforcing brand consistency.
- **Accessibility:** Sufficient contrast ratios (WCAG AA minimum), legible font sizes especially given part of the audience is older users, RTL-ready layout structure for future Arabic version.

---

## 8. Technical Requirements

- **Type:** Static multi-page marketing site (no backend/database required for v1).
- **Responsiveness:** Fully responsive — mobile-first, since most traffic will arrive from mobile (social media links, YouTube description links).
- **Performance:** Fast load time is critical for conversion (target: LCP under 2.5s); optimize all images/screenshots.
- **SEO:** Each page needs unique meta title/description; structured data (schema.org SoftwareApplication) on Home and Download pages to support rich search results; sitemap.xml and robots.txt.
- **Analytics-ready:** Structure should allow easy insertion of an analytics snippet later (e.g., Plausible/GA) without redesign.
- **Deployment target:** Static hosting compatible (e.g., can be built as plain HTML/CSS/JS, or React if Zaid prefers consistency with his existing stack).
- **CTA tracking:** Download buttons should be structured (consistent class/id naming) to allow later click-tracking instrumentation.

---

## 9. Content & Assets Needed From Zaid Before Final Build

- [ ] Real app screenshots (player, QR linking screen, offline manager, local library, settings)
- [ ] Final app icon/logo (high-res, transparent background)
- [ ] Actual Google Play Store link (if published) and/or Tatbiqati direct link and/or APK file location
- [ ] Confirmed minimum Android version / app size / current version number
- [ ] Real answers for the Privacy Policy page (data handling, MQTT data, third-party services)
- [ ] Reciters/audio sources actually supported (for accurate FAQ/feature copy)
- [ ] Confirmation on whether Arabic version of the site is planned for a later phase

---

## 10. Success Metrics (Post-Launch)

- Click-through rate on "Download Now" CTA (target benchmark: to be set after baseline traffic is established).
- Bounce rate on Home page.
- Traffic sources (YouTube description links, ZaidHB Official/Market & Crypto social channels, direct/Tatbiqati referral).
- Page load performance (Core Web Vitals).

---

## 11. Open Questions / Risks

- **Risk:** Privacy Policy page cannot be finalized with placeholder content if the site is meant to go live and link from Google Play — this must be revisited with accurate, real information.
- **Risk:** MQTT sync as a headline feature requires the underlying broker infrastructure to be stable and always-on; site copy should avoid overpromising real-time guarantees if there are known reliability caveats from the July 2026 audit.
- **Open question:** Primary distribution channel — is Google Play the main target, or is Tatbiqati (Zaid's own APK hub) the primary/only channel for v1? This affects the Download page structure significantly.

---

*End of PRD. Ready to proceed to visual design (e.g., Google Stitch mockups) and/or direct implementation (HTML/React) upon Zaid's confirmation of Section 9 assets and Section 11 open questions.*
