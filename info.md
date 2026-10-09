# 🌸 Orchidia — Fashion Shopping App

**Orchidia** adalah aplikasi mobile e-commerce fashion yang dikembangkan menggunakan Kotlin untuk memudahkan pengguna menjelajahi produk fashion, memilih barang, mengelola keranjang belanja, dan melakukan simulasi pembayaran melalui antarmuka yang modern dan feminin.

Aplikasi ini mengusung konsep visual **Y2K, girly, dan igari-inspired** dengan kombinasi warna putih, fuchsia pink, dan silver.

## ✨ Fitur Aplikasi

* **Product Catalog** — Menampilkan daftar produk fashion beserta gambar, nama, harga, dan rating.
* **Product Search** — Memudahkan pengguna mencari produk berdasarkan nama.
* **Product Categories** — Mengelompokkan produk berdasarkan kategori fashion.
* **Product Detail** — Menampilkan informasi produk yang dipilih.
* **Shopping Cart** — Menambahkan produk ke keranjang, mengubah jumlah barang, dan menghapus produk.
* **Checkout** — Menampilkan ringkasan pesanan, alamat pengiriman, pilihan ekspedisi, dan total pembayaran.
* **Payment Simulation** — Menyediakan pilihan metode pembayaran serta simulasi BCA Virtual Account, countdown pembayaran, dan pengecekan status pembayaran.
* **Order History** — Menyimpan dan menampilkan riwayat pesanan.
* **Supabase Integration** — Mengambil data produk serta menyimpan data keranjang, pesanan, dan detail item pesanan ke backend.

## 🛠️ Teknologi yang Digunakan

| Teknologi      | Kegunaan                                    |
| -------------- | ------------------------------------------- |
| Kotlin         | Bahasa pemrograman aplikasi Android         |
| Android Studio | IDE untuk pengembangan aplikasi             |
| XML            | Membuat layout dan antarmuka aplikasi       |
| Retrofit       | Menghubungkan aplikasi dengan REST API      |
| Supabase       | Backend dan database aplikasi               |
| Gson           | Mengonversi data JSON                       |
| Coroutines     | Menjalankan proses asynchronous             |
| Git & GitHub   | Version control dan penyimpanan source code |

## 🚀 Cara Menjalankan Aplikasi

### Persyaratan

* Android Studio.
* JDK yang sesuai dengan konfigurasi project.
* Perangkat Android atau emulator dengan Android API yang memenuhi `minSdk` project.
* Koneksi internet.
* Project Supabase yang sudah dikonfigurasi.

### Langkah Instalasi

1. Clone repository:

   ```bash
   git clone https://github.com/USERNAME/REPOSITORY.git
   ```

2. Buka Android Studio, kemudian pilih **Open** dan arahkan ke folder project.

3. Tunggu proses Gradle Sync hingga selesai.

4. Pastikan konfigurasi `BASE_URL` dan API key Supabase tersedia secara lokal dan sesuai dengan project backend.

5. Hubungkan perangkat Android atau jalankan emulator.

6. Klik **Run** untuk membangun dan menjalankan aplikasi.

### Konfigurasi Backend

Aplikasi menggunakan Supabase sebagai backend. Sebelum dijalankan, pastikan tabel database yang diperlukan sudah dibuat, yaitu:

* `products`
* `cart_items`
* `orders`
* `order_items`

Pastikan kebijakan Row Level Security (RLS) dan izin akses tabel sesuai dengan konfigurasi aplikasi.

**Catatan:** Jangan menyimpan secret key atau `service_role` key Supabase di source code maupun repository publik.

## 📸 Screenshot Aplikasi

Screenshot aplikasi dapat dilihat pada bagian berikut.

| Halaman                  | Screenshot                                  |
| ------------------------ | ------------------------------------------- |
| Landing Page             | Tambahkan screenshot landing page di sini   |
| Home                     | Tambahkan screenshot halaman Home           |
| Product Catalog / Search | Tambahkan screenshot katalog atau pencarian |
| Product Detail           | Tambahkan screenshot detail produk          |
| Shopping Cart            | Tambahkan screenshot keranjang              |
| Checkout                 | Tambahkan screenshot checkout               |
| Payment                  | Tambahkan screenshot pembayaran             |
| Payment Success          | Tambahkan screenshot pembayaran berhasil    |

Simpan gambar di folder `screenshots/`, kemudian gunakan format Markdown berikut:

```markdown
![Home Screen](screenshots/home.png)
```

## 📦 APK Aplikasi

**Download APK:** [Tambahkan link APK di sini]

File APK dapat dibagikan melalui GitHub Releases atau Google Drive dengan akses unduh yang sesuai.

## 🎬 Video Demo

**Video Demo Aplikasi:** [Tambahkan link video demo di sini]

Video demonstrasi memperlihatkan alur penggunaan aplikasi mulai dari membuka halaman Home, memilih produk, mengelola keranjang, melakukan checkout, memilih metode pembayaran, hingga menampilkan halaman pembayaran berhasil.

## 👩‍🎓 Identitas Mahasiswa

| Keterangan       | Informasi                |
| ---------------- | ------------------------ |
| Nama             | Farah Amelia             |
| Program Studi    | Rekayasa Perangkat Lunak |
| Perguruan Tinggi | STMIK IKMI Cirebon       |
| Mata Kuliah      | [Isi nama mata kuliah]   |
| Dosen Pengampu   | [Isi nama dosen]         |
| Tahun Akademik   | 2026                     |

## 📌 Catatan Pengembangan

Orchidia dikembangkan sebagai proyek pembelajaran pengembangan aplikasi Android berbasis Kotlin dan integrasi REST API. Pembayaran BCA Virtual Account dalam aplikasi merupakan **simulasi**, bukan transaksi atau verifikasi pembayaran BCA yang sebenarnya.

---

**Orchidia — Express Yourself Through Fashion 🌸**
