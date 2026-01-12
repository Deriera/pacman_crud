-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 05 Jan 2026 pada 02.55
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pacman_db`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `leaderboard`
--

CREATE TABLE `leaderboard` (
  `id` int(11) NOT NULL,
  `player_name` varchar(50) NOT NULL,
  `score` int(11) NOT NULL,
  `played_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `leaderboard`
--

INSERT INTO `leaderboard` (`id`, `player_name`, `score`, `played_at`) VALUES
(1, 'Player', 860, '2025-12-27 05:45:15'),
(2, 'Player', 650, '2026-01-05 01:31:40'),
(3, 'dasdas', 120, '2026-01-05 01:32:53'),
(4, 'Player', 620, '2026-01-05 01:41:13');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `leaderboard`
--
ALTER TABLE `leaderboard`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_score` (`score`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `leaderboard`
--
ALTER TABLE `leaderboard`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
