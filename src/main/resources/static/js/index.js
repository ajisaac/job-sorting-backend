"use strict";

window.onload = function () {
    var numJobsSpan = document.querySelector("span[data-num-jobs]");
    var numCompaniesSpan = document.querySelector("span[data-num-companies]");

    document.querySelectorAll("div.company-name-bar > h3").forEach(function (title) {
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

    document.querySelectorAll(".search-pane").forEach(function (eb) {
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

    document.querySelectorAll("button[data-job-action]").forEach(function (sb) {
        var actionType = sb.dataset.jobAction;
        sb.onclick = async function () {
            await changeJobState(sb, actionType);
        };
    });

    document.querySelectorAll("button[data-company-action]").forEach(function (ca) {
        var actionType = ca.dataset.companyAction;
        ca.onclick = async function () {
            await changeMultipleJobState(ca, actionType);
        };
    });

    document.querySelectorAll("button[data-job-edit]").forEach(function (eb) {
        eb.onclick = function () {
            try {
                var pre = eb.closest(".job-post").querySelector("pre");
                if (!pre) return;

                var preParent = pre.parentElement;
                var summary = pre.textContent;

                var tArea = document.createElement("textarea");
                tArea.appendChild(document.createTextNode(summary));

                tArea.oninput = function () {
                    var offset = tArea.offsetHeight - tArea.clientHeight;
                    tArea.style.height = 'auto';
                    tArea.style.height = tArea.scrollHeight + offset + 'px';
                };

                var submitButton = document.createElement("button");
                submitButton.classList.add("btn-link");
                submitButton.appendChild(document.createTextNode("submit"));
                submitButton.onclick = async function () {

                    try {

                        // set up
                        var summaryHolder = submitButton.closest(".summary");
                        if (!summaryHolder) return;
                        var textArea = summaryHolder.querySelector("textarea");
                        if (!textArea) return;
                        var newText = textArea.value;
                        if (!newText) return;
                        console.log(newText);

                        // server side update
                        var id = submitButton.closest("div[data-job-id]").dataset.jobId;
                        var url = '/jobs/summary/' + id;
                        await postData(url, { summary: newText });

                        // remove everything
                        if (textArea) textArea.remove();
                        var _buttonDiv = summaryHolder.querySelector(".summary-button-holder");
                        if (_buttonDiv) _buttonDiv.remove();

                        var _pre = document.createElement("pre");
                        _pre.appendChild(document.createTextNode(newText));
                        summaryHolder.appendChild(_pre);
                    } catch (err) {
                        console.log(err);
                    }
                };

                var cancelButton = document.createElement("button");
                cancelButton.classList.add("btn-link");
                cancelButton.appendChild(document.createTextNode("cancel"));
                cancelButton.onclick = function () {

                    var summaryHolder = cancelButton.closest(".summary");

                    var tArea = summaryHolder.querySelector("textarea");
                    if (tArea) tArea.remove();

                    var buttonDiv = summaryHolder.querySelector(".summary-button-holder");
                    if (buttonDiv) buttonDiv.remove();

                    var pre = document.createElement("pre");
                    pre.appendChild(document.createTextNode(summary));
                    summaryHolder.appendChild(pre);
                };

                pre.remove();
                preParent.appendChild(tArea);

                var buttonDiv = document.createElement("div");
                buttonDiv.classList.add("summary-button-holder");
                buttonDiv.appendChild(submitButton);
                buttonDiv.appendChild(cancelButton);

                preParent.appendChild(buttonDiv);

                tArea.focus();
                tArea.dispatchEvent(new Event("input"));
            } catch (err) {
                console.log(err);
            }
        };
    });

    document.querySelectorAll("button[data-company]").forEach(function (bl) {
        bl.onclick = async function () {
            try {
                var company = bl.dataset.company;
                if (!company) return;

                var url = '/jobs/blacklist/' + company;
                await postData(url, {});

                bl.closest(".company").remove();

                var blc = document.querySelector("div[data-blacklist-companies]");
                if (!blc) return;

                var d = document.createElement("div");
                d.textContent = company;

                var b = document.createElement("button");
                b.classList.add("btn-link", "btn-tiny");
                b.value = company;
                b.innerText = "remove?";
                b.onclick = async function () {
                    await deleteData(url);
                    var a = b.closest("div");
                    if (!a) return;
                    a.remove();
                };

                d.appendChild(b);
                blc.appendChild(d);
            } catch (err) {
                console.log(err);
            }
        };
    });

    document.querySelectorAll("button[data-remove-blacklisted-company]").forEach(function (rblc) {
        rblc.onclick = async function () {
            try {
                var company = rblc.value;
                if (!company) return;
                console.log(company);
                var url = '/jobs/blacklist/' + company;
                await deleteData(url);
                var a = rblc.closest("div");
                if (!a) return;
                a.remove();
            } catch (err) {
                console.log(err);
            }
        };
    });

    function lowerJobCount() {
        var amount = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;

        if (!amount || isNaN(amount)) return;
        var num = parseInt(numJobsSpan.textContent);
        if (!num || isNaN(num)) return;

        num = num - amount;
        numJobsSpan.textContent = num.toString();
    }

    function lowerCompanyCount() {
        var amount = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;

        if (!amount || isNaN(amount)) return;
        var num = parseInt(numCompaniesSpan.textContent);
        if (!num || isNaN(num)) return;

        num = num - amount;
        numCompaniesSpan.textContent = num.toString();
    }

    async function postData() {
        var url = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : '';
        var data = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};

        // Default options are marked with *
        var response = await fetch(url, {
            method: 'POST', // *GET, POST, PUT, DELETE, etc.
            mode: 'cors', // no-cors, *cors, same-origin
            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
            credentials: 'same-origin', // include, *same-origin, omit
            headers: {
                'Content-Type': 'application/json'
                // 'Content-Type': 'application/x-www-form-urlencoded',
            },
            redirect: 'follow', // manual, *follow, error
            referrer: 'no-referrer', // no-referrer, *client
            body: JSON.stringify(data) // body data type must match "Content-Type" header
        });
        if (!response.ok) {
            throw Error(response.statusText);
        }
        return response;
    }

    async function deleteData() {
        var url = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : '';

        var response = await fetch(url, {
            method: 'DELETE', // *GET, POST, PUT, DELETE, etc.
            mode: 'cors', // no-cors, *cors, same-origin
            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
            credentials: 'same-origin', // include, *same-origin, omit
            headers: {
                // 'Content-Type': 'application/json'
                // 'Content-Type': 'application/x-www-form-urlencoded',
            },
            redirect: 'follow', // manual, *follow, error
            referrer: 'no-referrer' // no-referrer, *client
            // body: JSON.stringify(data) // body data type must match "Content-Type" header
        });
        if (!response.ok) {
            throw Error(response.statusText);
        }
        return response;
    }

    // post to the server to change the state of one job
    async function changeJobState(sb, state) {
        if (!sb || !state) throw Error("Missing job or state.");
        try {

            var id = sb.dataset.jobId;
            if (!id) return;

            var url = '/jobs/state/' + state + '/' + id;
            await postData(url, {});

            var compContent = sb.closest(".company-content");
            var company = sb.closest(".company");

            sb.closest("div[data-job-id='" + id + "']").remove();
            if (compContent.children.length <= 0) {
                company.remove();
                lowerCompanyCount(1);
            }
            lowerJobCount(1);
        } catch (error) {
            console.log(error);
        }
    }

    // post to the server to change the state of multiple jobs
    async function changeMultipleJobState(ca, state) {
        if (!ca || !state) throw Error("Missing company or state.");
        try {

            // get all our job ids
            var jobs = Array.from(ca.closest(".company").querySelectorAll("div[data-job-id]"));

            var jobIds = jobs.map(function (j) {
                var id = j.dataset.jobId;
                if (id) {
                    return id;
                }
                return 0;
            });

            var url = '/jobs/multiplestates/' + state;
            await postData(url, jobIds);

            lowerJobCount(jobs.length);
            lowerCompanyCount(1);

            ca.closest(".company").remove();
        } catch (error) {
            console.log(error);
        }
    }
};
