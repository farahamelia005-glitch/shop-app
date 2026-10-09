# Orchidia — Fashion Shopping App

Orchidia adalah aplikasi belanja fashion berbasis Android dengan visual feminin, Y2K, dan igari. Aplikasi ini dibuat sebagai proyek UAS Pemrograman Mobile Dasar.

## Identitas Proyek

- **Nama mahasiswa:** Farah Amelia
- **NIM:** 42240183
- **Program studi / kelas:** Rekayasa Perangkat Lunak / 5A-2024
- **Mata kuliah:** Pemrograman Mobile Dasar
- **Nama aplikasi:** Orchidia — Fashion Shopping App
- **Repositori:** https://github.com/farahamelia005-glitch/shop-app
- **APK:** https://drive.google.com/file/d/18dzSWTekmv7iR2fZkKWHl_k9xTkySv6U/view?usp=drive_link
- **Video demo:** [Tempel tautan YouTube/Google Drive]
- **Laporan:** [Tempel tautan Google Drive]

## Deskripsi

Orchidia merupakan prototipe e-commerce fashion untuk menjelajahi produk, mengelola keranjang belanja, melakukan checkout, dan mencoba alur pembayaran simulasi. Gaya visual aplikasi terinspirasi dari estetika feminin, Y2K, dan igari dengan aksen warna pink dan silver.

## Fitur

- Katalog produk fashion.
- Pencarian dan kategori produk.
- Halaman detail produk.
- Keranjang belanja: tambah item, ubah kuantitas, dan hapus item.
- Pengelolaan keranjang terhubung ke Supabase.
- Alur alamat pengiriman dan checkout.
- Perhitungan subtotal, ongkos kirim, dan total pembayaran.
- Simulasi pembayaran menggunakan BCA Virtual Account demo `123 456 7890`, hitung mundur, dan tombol **Cek Status Pembayaran**.
- Penyimpanan data pesanan dan item pesanan ke Supabase.
- Halaman pembayaran berhasil dan riwayat pesanan lokal.

> **Catatan pembayaran:** BCA Virtual Account pada aplikasi ini hanya data simulasi untuk kebutuhan demonstrasi. Aplikasi tidak memproses transaksi perbankan sungguhan.

## Teknologi

- Kotlin
- Android Studio
- Android XML Layout
- Retrofit 2.11.0
- OkHttp
- Gson Converter
- Kotlin Coroutines / lifecycleScope
- Supabase REST API dan PostgreSQL
- Git dan GitHub

## Struktur data Supabase

Tabel yang digunakan pada integrasi proyek:
- `products` — data katalog produk.
- `cart_items` — item dan kuantitas keranjang.
- `orders` — informasi pesanan, alamat, pengiriman, total, dan status.
- `order_items` — rincian produk di setiap pesanan.

## Cara Menjalankan Aplikasi

### Instalasi menggunakan APK

1. Buka tautan Google Drive APK pada bagian **APK** di atas.
2. Unduh file APK ke perangkat Android.
3. Jika diminta, izinkan instalasi aplikasi dari sumber yang digunakan untuk membuka file.
4. Instal APK dan jalankan **Orchidia**.
5. Pastikan perangkat terhubung ke internet agar fitur yang menggunakan Supabase dapat diakses.

### Menjalankan source code

1. Clone repository:
   ```bash
   git clone https://github.com/farahamelia005-glitch/shop-app.git
   ```
2. Buka folder proyek di Android Studio.
3. Tunggu Gradle Sync selesai.
4. Pastikan konfigurasi Supabase menggunakan URL proyek dan **publishable/anon key** yang sesuai.
5. Jangan menaruh `service_role` key atau secret key di source code Android.
6. Jalankan aplikasi pada perangkat Android atau emulator yang kompatibel.

## Pengujian

Sebelum mengumpulkan, uji katalog, pencarian, tambah keranjang, ubah kuantitas, hapus item, checkout, simulasi pembayaran, dan penyimpanan order. Isi hasil pengujian berdasarkan perilaku APK final dan verifikasi tabel `orders` serta `order_items` di Supabase.

## Tautan Proyek

- **GitHub:** https://github.com/farahamelia005-glitch/shop-app
- **APK:** https://drive.google.com/file/d/18dzSWTekmv7iR2fZkKWHl_k9xTkySv6U/view?usp=drive_link
- **Video demo (maksimal 15 menit, facecam dan suara asli):** [Tempel tautan]
- **Laporan UAS:** [Tempel tautan Google Drive]

---

*Orchidia — Find Your Style, Express Yourself.*
