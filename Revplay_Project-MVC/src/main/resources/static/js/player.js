// --- Global Player Controller ---
const PlayerController = (() => {
    // UI Elements
    let audioPlayer, playerContainer, playerAlbumArt, playerTitle, playerArtist;
    let playPauseBtn, playIcon, pauseIcon, favoriteBtn, nextBtn, prevBtn, shuffleBtn, repeatBtn;
    let progressBar, currentTimeEl, totalTimeEl, volumeSlider;

    let isPlaying = false;
    let currentSongIndex = -1;
    let playlist = [];
    let originalPlaylist = [];
    let isFavorite = false;
    let shuffleMode = false;
    let repeatMode = 0; // 0: Off, 1: Repeat All, 2: Repeat One

    const PROXY_URL = "/api/proxy";
    const DIRECT_BACKEND = "http://localhost:8080";

    function init() {
        audioPlayer = document.getElementById("global-audio-element");
        playerContainer = document.getElementById("global-player-container");
        playerAlbumArt = document.getElementById("player-album-art");
        playerTitle = document.getElementById("player-title");
        playerArtist = document.getElementById("player-artist");
        playPauseBtn = document.getElementById("player-play-pause-btn");
        playIcon = document.getElementById("player-play-icon");
        pauseIcon = document.getElementById("player-pause-icon");
        favoriteBtn = document.getElementById("player-favorite-btn");
        nextBtn = document.getElementById("player-next-btn");
        prevBtn = document.getElementById("player-prev-btn");
        shuffleBtn = document.getElementById("player-shuffle-btn");
        repeatBtn = document.getElementById("player-repeat-btn");
        progressBar = document.getElementById("player-progress-bar");
        currentTimeEl = document.getElementById("player-current-time");
        totalTimeEl = document.getElementById("player-total-time");
        volumeSlider = document.getElementById("player-volume-slider");

        if (!audioPlayer) return;

        setupEventListeners();
        buildPlaylist();
    }

    function setupEventListeners() {
        if (playPauseBtn && playPauseBtn.getAttribute('data-listener')) return;

        if (playPauseBtn) playPauseBtn.addEventListener("click", () => togglePlay());
        if (nextBtn) nextBtn.addEventListener("click", playNext);
        if (prevBtn) prevBtn.addEventListener("click", playPrev);
        if (audioPlayer) audioPlayer.addEventListener("ended", playNext);

        if (shuffleBtn) {
            shuffleBtn.addEventListener("click", () => {
                shuffleMode = !shuffleMode;
                shuffleBtn.classList.toggle("active", shuffleMode);
                const currentSong = playlist[currentSongIndex];
                buildPlaylist();
                if (currentSong) {
                    currentSongIndex = playlist.findIndex(s => s.id === currentSong.id);
                }
            });
        }

        if (repeatBtn) {
            repeatBtn.addEventListener("click", () => {
                repeatMode = (repeatMode + 1) % 3;
                repeatBtn.classList.remove("active", "active-all", "active-one");
                if (repeatMode === 1) repeatBtn.classList.add("active", "active-all");
                else if (repeatMode === 2) repeatBtn.classList.add("active", "active-one");
            });
        }

        if (audioPlayer) audioPlayer.addEventListener("timeupdate", updateProgress);
        if (progressBar) progressBar.addEventListener("input", seek);
        if (volumeSlider) volumeSlider.addEventListener("input", updateVolume);
        if (favoriteBtn) favoriteBtn.addEventListener("click", toggleFavorite);

        if (playPauseBtn) playPauseBtn.setAttribute('data-listener', 'true');
    }

    function buildPlaylist() {
        const songButtons = document.querySelectorAll(".play-overlay-btn");
        const newPlaylist = [];
        songButtons.forEach((btn, index) => {
            newPlaylist.push({
                id: btn.getAttribute("data-song-id"),
                title: btn.getAttribute("data-song-title"),
                artist: btn.getAttribute("data-song-artist"),
                file: btn.getAttribute("data-song-file"),
                cover: btn.getAttribute("data-song-cover"),
                index: index
            });
        });
        originalPlaylist = [...newPlaylist];
        playlist = shuffleMode ? shuffleArray([...newPlaylist]) : newPlaylist;
    }

    function shuffleArray(array) {
        for (let i = array.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [array[i], array[j]] = [array[j], array[i]];
        }
        return array;
    }

    function loadSong(song) {
        if (!song) return;
        fetch(`${PROXY_URL}/play/${song.id}`, { method: 'POST' }).catch(() => { });

        let audioSrc = song.file;
        if (!audioSrc.startsWith("http")) audioSrc = DIRECT_BACKEND + (audioSrc.startsWith("/") ? "" : "/") + audioSrc;
        audioPlayer.src = audioSrc;

        playerTitle.textContent = song.title;
        playerArtist.textContent = song.artist;
        let coverSrc = song.cover || "/images/default-cover.png";
        if (!coverSrc.startsWith("http") && !coverSrc.startsWith("data:") && !coverSrc.startsWith("/images/default")) {
            coverSrc = DIRECT_BACKEND + (coverSrc.startsWith("/") ? "" : "/") + coverSrc;
        }
        playerAlbumArt.src = coverSrc;

        isFavorite = false;
        updateFavoriteIcon();
        playerContainer.style.display = "flex";
        togglePlay(true);
    }

    function togglePlay(forcePlay = null) {
        if (forcePlay === true || (forcePlay === null && !isPlaying)) {
            if (!audioPlayer.src) return;
            audioPlayer.play().then(() => {
                isPlaying = true;
                if (playIcon) playIcon.style.display = "none";
                if (pauseIcon) pauseIcon.style.display = "inline-block";
            }).catch(e => console.error("Playback failed:", e));
        } else {
            audioPlayer.pause();
            isPlaying = false;
            if (playIcon) playIcon.style.display = "inline-block";
            if (pauseIcon) pauseIcon.style.display = "none";
        }
    }

    function playNext() {
        if (playlist.length === 0) buildPlaylist();
        if (playlist.length === 0) return;
        if (repeatMode === 2) {
            audioPlayer.currentTime = 0;
            togglePlay(true);
            return;
        }
        currentSongIndex++;
        if (currentSongIndex >= playlist.length) {
            if (repeatMode === 1) currentSongIndex = 0;
            else { currentSongIndex = -1; togglePlay(false); return; }
        }
        loadSong(playlist[currentSongIndex]);
    }

    function playPrev() {
        if (playlist.length === 0) buildPlaylist();
        if (playlist.length === 0) return;
        currentSongIndex--;
        if (currentSongIndex < 0) currentSongIndex = repeatMode === 1 ? playlist.length - 1 : 0;
        loadSong(playlist[currentSongIndex]);
    }

    function updateProgress() {
        const current = audioPlayer.currentTime;
        const duration = audioPlayer.duration;
        if (currentTimeEl) currentTimeEl.textContent = formatTime(current);
        if (duration) {
            if (totalTimeEl) totalTimeEl.textContent = formatTime(duration);
            const percentage = (current / duration) * 100;
            if (progressBar) {
                progressBar.value = percentage;
                progressBar.style.setProperty('--value', `${percentage}%`);
            }
        }
    }

    function seek(e) {
        const duration = audioPlayer.duration;
        if (duration) {
            audioPlayer.currentTime = (e.target.value / 100) * duration;
        }
    }

    function updateVolume(e) {
        audioPlayer.volume = e.target.value / 100;
        if (volumeSlider) volumeSlider.style.setProperty('--value', `${e.target.value}%`);
    }

    function formatTime(seconds) {
        if (isNaN(seconds)) return "0:00";
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
    }

    function toggleFavorite() {
        const song = playlist[currentSongIndex];
        if (!song) return;
        const method = isFavorite ? 'DELETE' : 'POST';
        fetch(`${PROXY_URL}/favorites/${song.id}`, { method: method })
            .then(res => {
                if (res.ok) {
                    isFavorite = !isFavorite;
                    updateFavoriteIcon();
                    showNotification(isFavorite ? "Added to favorites!" : "Removed from favorites!");
                }
            });
    }

    function updateFavoriteIcon() {
        if (!favoriteBtn) return;
        if (isFavorite) {
            favoriteBtn.innerHTML = '<i class="fa-solid fa-heart" style="color: var(--accent-yellow);"></i>';
            favoriteBtn.classList.add('active');
        } else {
            favoriteBtn.innerHTML = '<i class="fa-regular fa-heart"></i>';
            favoriteBtn.classList.remove('active');
        }
    }

    function showNotification(msg) {
        console.log("Notification:", msg);
        alert(msg);
    }

    // --- Public API ---
    return {
        init: init,
        reinit: buildPlaylist,
        playSong: (btn) => {
            buildPlaylist();
            const songId = btn.getAttribute("data-song-id");
            currentSongIndex = playlist.findIndex(s => s.id === songId);
            if (currentSongIndex !== -1) loadSong(playlist[currentSongIndex]);
        }
    };
})();

document.addEventListener("DOMContentLoaded", () => {
    PlayerController.init();
});

// Expose globals for onclick handlers
window.playSong = PlayerController.playSong;
window.reinitPlayerContent = PlayerController.reinit;

// --- Options Dropdown and Auth logic ---
window.toggleOptions = function (btn, event) {
    event.stopPropagation();
    const dropdown = btn.nextElementSibling;
    const isShown = dropdown.classList.contains("show");
    document.querySelectorAll(".options-dropdown.show").forEach(d => d.classList.remove("show"));
    if (!isShown) dropdown.classList.add("show");
};

window.addFavorite = function (songId) {
    fetch(`/api/proxy/favorites/${songId}`, { method: 'POST' })
        .then(res => {
            if (res.ok) {
                alert("Added to favorites!");
                location.reload();
            }
        });
};

window.removeFavorite = function (songId) {
    fetch(`/api/proxy/favorites/${songId}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) {
                alert("Removed from favorites!");
                location.reload();
            }
        });
};

window.showPlaylistModal = function (songId) {
    const modal = document.getElementById("playlist-modal");
    const list = document.getElementById("playlist-selection-list");
    if (!modal) return;
    modal.classList.add("show");
    fetch(`/api/proxy/playlists/my`)
        .then(res => res.json())
        .then(data => {
            list.innerHTML = "";
            if (!data || data.length === 0) list.innerHTML = "<p>No playlists found.</p>";
            else data.forEach(pl => {
                const btn = document.createElement("button");
                btn.className = "playlist-select-item";
                btn.innerHTML = `<i class="fa-solid fa-music"></i> <span>${pl.name}</span>`;
                btn.onclick = () => {
                    fetch(`/api/proxy/playlists/${pl.id}/songs/${songId}`, { method: 'PUT' })
                        .then(res => { if (res.ok) { alert("Added to playlist!"); document.getElementById("playlist-modal").classList.remove("show"); } });
                };
                list.appendChild(btn);
            });
        });
};

window.closePlaylistModal = () => {
    const modal = document.getElementById("playlist-modal");
    if (modal) modal.classList.remove("show");
};

document.addEventListener("click", () => document.querySelectorAll(".options-dropdown.show").forEach(d => d.classList.remove("show")));
