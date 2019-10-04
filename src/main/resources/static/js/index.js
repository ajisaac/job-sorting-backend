"use strict";

window.onload = function () {
    var bars = document.querySelectorAll("div.company-name-bar-title > h3");
    bars.forEach(function (title) {
        title.onclick = function () {
            var content = title.closest("div.company-name-bar").nextElementSibling;
            if (content !== null) {
                if (content.classList.contains("company-content")) {
                    var isHidden = content.classList.contains("hidden");
                    if (isHidden) {
                        content.classList.remove("hidden");
                    } else {
                        content.classList.add("hidden");
                    }
                }
            }
        };
    });

    var expandableBoxes = document.querySelectorAll(".search-term-box");
    expandableBoxes.forEach(function (eb) {
        var title = eb.querySelector("h4.expandable");
        if (title !== null) {
            title.onclick = function () {
                var toHide = eb.querySelector(".expandable-content");
                if (toHide !== null) {
                    var isHidden = toHide.classList.contains("hidden");
                    if (isHidden) {
                        toHide.classList.remove("hidden");
                    } else {
                        toHide.classList.add("hidden");
                    }
                }
            };
        }
    });
};
