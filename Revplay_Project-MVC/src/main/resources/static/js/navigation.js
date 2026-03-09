// --- Persistent Navigation (SPA-lite) ---
const NavigationController = (() => {
    function init() {
        document.body.addEventListener("click", handleLinkClick);
        window.addEventListener("popstate", handlePopState);

        // --- Add Global Dropdown Toggle Logic ---
        document.addEventListener('click', function (e) {
            // Check for filter-btn or anything inside it
            const filterBtn = e.target.closest('.filter-btn');
            if (filterBtn) {
                e.stopPropagation();
                const menu = filterBtn.nextElementSibling;
                if (!menu) return;

                // Close other menus of same type
                document.querySelectorAll('.filter-menu').forEach(m => {
                    if (m !== menu) m.classList.remove('show');
                });
                menu.classList.toggle('show');
                return;
            }

            // Check for options-btn or anything inside it
            const optionsBtn = e.target.closest('.options-btn');
            if (optionsBtn) {
                e.stopPropagation();
                const dropdown = optionsBtn.nextElementSibling;
                if (!dropdown) return;

                // Close other dropdowns
                document.querySelectorAll('.options-dropdown').forEach(d => {
                    if (d !== dropdown) d.classList.remove('show');
                });
                dropdown.classList.toggle('show');
                return;
            }

            // Global modal triggers
            const createTrigger = e.target.closest('.create-playlist-trigger');
            if (createTrigger) {
                const modal = document.getElementById('createPlaylistModal');
                if (modal) modal.classList.add('show');
                return;
            }

            const closeBtn = e.target.closest('.close-modal');
            if (closeBtn) {
                const modal = closeBtn.closest('.modal');
                if (modal) modal.classList.remove('show');
                return;
            }

            // Clickable Playlist Card
            const playlistCard = e.target.closest('.clickable-playlist');
            if (playlistCard && !e.target.closest('.song-options')) {
                const url = playlistCard.getAttribute('data-url');
                if (url) NavigationController.navigate(url);
                return;
            }

            // Close everything on document click (outside of menus/modals)
            if (!e.target.closest('.modal-content') && !e.target.closest('.filter-dropdown') && !e.target.closest('.song-options')) {
                document.querySelectorAll('.filter-menu, .options-dropdown, .modal').forEach(m => {
                    m.classList.remove('show');
                });
            }
        });

        interceptForms();
    }

    async function handleLinkClick(e) {
        const link = e.target.closest("a");
        if (!link) return;

        const href = link.getAttribute("href");
        if (!href || href.startsWith("#") || href.startsWith("javascript:") || link.target === "_blank") return;

        // Only AJAX internal user paths (avoid logout, external, etc.)
        if (href.includes("/user/") || href.includes("/search")) {
            e.preventDefault();
            navigateTo(href);
        }
    }

    async function navigateTo(url, push = true) {
        try {
            console.log("Navigating to:", url);
            const response = await fetch(url);
            if (!response.ok) throw new Error("Navigation failed");

            const html = await response.text();
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, "text/html");

            // 1. Update Title
            document.title = doc.title;

            // 2. Replace Main Content
            const newContent = doc.querySelector(".main-content");
            const currentContent = document.querySelector(".main-content");
            if (newContent && currentContent) {
                currentContent.innerHTML = newContent.innerHTML;

                // Scroll to top
                currentContent.scrollTop = 0;
                const scrollArea = currentContent.querySelector(".content-scroll");
                if (scrollArea) scrollArea.scrollTop = 0;
            }

            // 3. Update Sidebar Active State
            updateSidebar(url);

            // 4. Update URL
            if (push) history.pushState({ url }, doc.title, url);

            // 5. Re-initialize Dynamic Content (Songs, etc.)
            if (window.reinitPlayerContent) {
                window.reinitPlayerContent();
            }

            // --- Execute Scripts in new content ---
            const scripts = currentContent.querySelectorAll("script");
            scripts.forEach(oldScript => {
                try {
                    const newScript = document.createElement("script");
                    if (oldScript.src) {
                        newScript.src = oldScript.src;
                    } else {
                        newScript.textContent = oldScript.textContent;
                    }
                    document.body.appendChild(newScript);
                    newScript.remove(); // Keep DOM clean
                } catch (scriptErr) {
                    console.error("Error executing injected script:", scriptErr);
                }
            });

            // 6. Re-bind forms in new content
            interceptForms();

        } catch (err) {
            console.error("AJAX Navigation Error:", err);
            window.location.href = url; // Fallback to full reload
        }
    }

    function handlePopState(e) {
        if (e.state && e.state.url) {
            navigateTo(e.state.url, false);
        } else {
            // If no state, try current URL
            navigateTo(window.location.pathname + window.location.search, false);
        }
    }

    function updateSidebar(url) {
        const sidebarLinks = document.querySelectorAll(".sidebar .nav-link");
        sidebarLinks.forEach(link => {
            const href = link.getAttribute("href");
            if (url.includes(href)) {
                link.classList.add("active");
                link.classList.remove("inactive");
            } else {
                link.classList.remove("active");
                link.classList.add("inactive");
            }
        });
    }

    function interceptForms() {
        const forms = document.querySelectorAll("form.search-form");
        forms.forEach(form => {
            if (form.getAttribute("data-nav-intercept")) return;
            form.addEventListener("submit", async (e) => {
                e.preventDefault();
                const formData = new FormData(form);
                const params = new URLSearchParams(formData);
                const url = `${form.action}?${params.toString()}`;
                navigateTo(url);
            });
            form.setAttribute("data-nav-intercept", "true");
        });
    }

    return {
        init: init,
        navigate: navigateTo
    };
})();

document.addEventListener("DOMContentLoaded", () => {
    NavigationController.init();
});

// Expose for external use
window.ajaxNavigate = NavigationController.navigate;
