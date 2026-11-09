# NEXCHAT Architecture & Full-Stack Deployment Guide

NEXCHAT is a next-generation, high-performance instant messaging platform built with Jetpack Compose for Android, with an architecture designed for Supabase / PostgreSQL backend, WebRTC audio/video calling, and Signal-compatible End-to-End Encryption (E2EE).

## 1. System Architecture
- **Client**: Native Android (Kotlin + Jetpack Compose + Coroutines/Flow + M3)
- **Backend / Realtime**: Supabase (PostgreSQL 15+, Supabase Realtime WebSocket engine, GoTrue Auth, Storage Buckets)
- **Audio & Video Engine**: WebRTC (STUN/TURN mesh signaling via Supabase Realtime Channels)
- **Security & Privacy**:
  - E2EE Identity keys, Signed PreKeys, One-Time Prekeys exchange
  - ChaCha20-Poly1305 / AES-256-GCM symmetric message encryption
  - Row Level Security (RLS) ensuring strict multi-tenant authorization
  - Zero-knowledge admin interface: telemetry, active user counts, reports without exposing message payloads

## 2. Supabase Setup Instructions
1. Create a Supabase Project at [supabase.com](https://supabase.com).
2. Open the SQL Editor in your Supabase dashboard and run `/schema.sql`.
3. In Authentication -> Providers:
   - Enable Phone (SMS OTP via Twilio or MessageBird).
   - Enable Email / Password with secure verification.
4. In Storage:
   - Create private buckets: `chat-attachments`, `status-media`, `avatars`.
   - Apply bucket policies referencing conversation membership.
5. In Project Settings -> API:
   - Copy `Project URL` and `anon public` key to `.env` or app settings.

## 3. WebRTC Signaling Overview
- `calls` table triggers real-time updates when an incoming call is created.
- Participants exchange ICE candidates and SDP offers/answers via Supabase Realtime broadcast channels (`call:roomId`).

## 4. Admin Dashboard
- Accessible to users with `is_admin = true`.
- Aggregates platform statistics, active user metrics, report handling, and user suspension.
