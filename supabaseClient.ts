import { createClient } from '@supabase/supabase-js';

// Production Supabase Credentials for NEXCHAT
const SUPABASE_URL = process.env.NEXT_PUBLIC_SUPABASE_URL ||
  process.env.VITE_SUPABASE_URL ||
  process.env.SUPABASE_URL ||
  'https://refaejedfgunfioxhdzu.supabase.co';

const SUPABASE_ANON_KEY = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY ||
  process.env.VITE_SUPABASE_ANON_KEY ||
  process.env.SUPABASE_ANON_KEY ||
  'sb_publishable_V4jW9ynXUS_LCj7b3Xvoig_W2oY2jo3';

if (!SUPABASE_URL || !SUPABASE_ANON_KEY) {
  console.warn('⚠️ Missing Supabase environment variables. Falling back to default project configuration.');
}

/**
 * Initialized Supabase client for NEXCHAT frontend (Web, PWA, React).
 * Provides access to Auth, Realtime channels, Postgres database, and Storage buckets.
 */
export const supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY, {
  auth: {
    persistSession: true,
    autoRefreshToken: true,
    detectSessionInUrl: true,
  },
  realtime: {
    params: {
      eventsPerSecond: 20,
    },
  },
});

export default supabase;
