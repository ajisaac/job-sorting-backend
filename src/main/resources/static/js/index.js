"use strict";

window.onload = function () {

    // hide show contents of a company
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

    // hide show search pane contents
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

    // click on job state button for job
    document.querySelectorAll("button[data-job-action]").forEach(function (sb) {
        var state = sb.dataset.jobAction;
        sb.onclick = async function () {
            if (!sb || !state) throw Error("Missing job or state.");
            try {

                var id = sb.dataset.jobId;
                if (!id) return;

                var url = '/jobs/state/' + state + '/' + id;
                await postData(url, {});

                location.reload();
            } catch (error) {
                console.log(error);
            }
        };
    });

    // click on job state button for company
    document.querySelectorAll("button[data-company-action]").forEach(function (ca) {
        ca.onclick = async function () {
            var state = ca.dataset.companyAction;
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

                location.reload();
            } catch (error) {
                console.log(error);
            }
        };
    });

    // our mass ignore button
    if (document.querySelector("button[data-bulk-action]")) {
        // click on job state button for the whole page
        document.querySelector("button[data-bulk-action]").onclick = async function (e) {
            var ca = e.target;
            console.log(e);
            var state = ca.dataset.bulkAction;
            console.log(ca);
            if (!ca || !state) throw Error("Missing company or state.");
            try {

                // get all our job ids
                var jobs = Array.from(ca.closest(".root").querySelectorAll("div[data-job-id]"));

                var jobIds = jobs.map(function (j) {
                    var id = j.dataset.jobId;
                    if (id) {
                        return id;
                    }
                    return 0;
                });

                var url = '/jobs/multiplestates/' + state;
                console.log(jobIds.length);
                // await postData(url, jobIds);

                location.reload();
            } catch (error) {
                console.log(error);
            }
        };
    }

    // edit the contents of a job
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

    // blacklist a company
    document.querySelectorAll("button[data-company]").forEach(function (bl) {
        bl.onclick = async function () {
            try {
                var company = bl.dataset.company;
                if (!company) return;

                var url = '/jobs/blacklist/' + company;
                await postData(url, {});

                location.reload();
            } catch (err) {
                console.log(err);
            }
        };
    });

    // remove a blacklisted company
    document.querySelectorAll("button[data-remove-blacklisted-company]").forEach(function (rblc) {
        rblc.onclick = async function () {
            try {
                var company = rblc.value;
                if (!company) return;

                var url = '/jobs/blacklist/' + company;
                await deleteData(url);

                location.reload();
            } catch (err) {
                console.log(err);
            }
        };
    });

    // click the include button for the title filter search pane
    document.querySelector("input[name='blockTitleChecked']").onclick = async function (e) {
        try {

            var checked = e.target.checked;
            if (checked === undefined || checked === null) return;

            var url = '/jobs/titlefilter/' + checked;
            await postData(url, {});

            location.reload();
        } catch (err) {
            console.log(err);
        }
    };

    // select one of the label filter inputs
    document.querySelectorAll("input[data-label-filter]").forEach(function (dlf) {
        dlf.onclick = async function () {
            try {
                var checked = dlf.checked;
                if (checked === undefined || checked === null) return;

                var filter = dlf.dataset.labelFilter;
                if (!filter) return;

                var url = '/jobs/labelfilter/' + filter + "/" + checked;
                await postData(url, {});
                location.reload();
            } catch (err) {
                console.log(err);
            }
        };
    });

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
};
