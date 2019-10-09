window.onload = () => {

    // hide show contents of a company
    document.querySelectorAll("div.company-name-bar > h3").forEach(title => {
        title.onclick = () => {
            let content = title.closest("div.company-name-bar").nextElementSibling;
            if (content !== null) {
                if (content.classList.contains("company-content")) {
                    let isHidden = content.classList.contains("hidden");
                    if (isHidden) {
                        content.classList.remove("hidden");
                    } else {
                        content.classList.add("hidden");
                    }
                }
            }
        }
    });

    // hide show search pane contents
    document.querySelectorAll(".search-pane").forEach(eb => {
        let title = eb.querySelector("h4.expandable");
        if (title !== null) {
            title.onclick = () => {
                let toHide = eb.querySelector(".expandable-content");
                if (toHide !== null) {
                    let isHidden = toHide.classList.contains("hidden");
                    if (isHidden) {
                        toHide.classList.remove("hidden");
                    } else {
                        toHide.classList.add("hidden");
                    }
                }
            }
        }
    });

    // click on job state button for job
    document.querySelectorAll("button[data-job-action]").forEach(sb => {
        let state = sb.dataset.jobAction;
        sb.onclick = async () => {
            if (!sb || !state) throw Error("Missing job or state.");
            try {

                let id = sb.dataset.jobId;
                if (!id) return;

                let url = '/jobs/state/' + state + '/' + id;
                await postData(url, {});

                location.reload();

            } catch (error) {
                console.log(error);
            }
        }
    });

    // click on job state button for company
    document.querySelectorAll("button[data-company-action]").forEach(ca => {
        ca.onclick = async () => {
            let state = ca.dataset.companyAction;
            if (!ca || !state) throw Error("Missing company or state.");
            try {

                // get all our job ids
                let jobs = Array.from(ca.closest(".company").querySelectorAll("div[data-job-id]"));

                let jobIds = jobs.map(j => {
                    let id = j.dataset.jobId;
                    if (id) {
                        return id;
                    }
                    return 0;
                });

                let url = '/jobs/multiplestates/' + state;
                await postData(url, jobIds);

                location.reload();
            } catch (error) {
                console.log(error);
            }

        }
    });

    // edit the contents of a job
    document.querySelectorAll("button[data-job-edit]").forEach(eb => {
        eb.onclick = () => {
            try {
                let pre = eb.closest(".job-post").querySelector("pre");
                if (!pre) return;

                let preParent = pre.parentElement;
                let summary = pre.textContent;

                let tArea = document.createElement("textarea");
                tArea.appendChild(document.createTextNode(summary));

                tArea.oninput = () => {
                    let offset = tArea.offsetHeight - tArea.clientHeight;
                    tArea.style.height = 'auto';
                    tArea.style.height = tArea.scrollHeight + offset + 'px';
                };

                let submitButton = document.createElement("button");
                submitButton.classList.add("btn-link");
                submitButton.appendChild(document.createTextNode("submit"));
                submitButton.onclick = async () => {

                    try {

                        // set up
                        let summaryHolder = submitButton.closest(".summary");
                        if (!summaryHolder) return;
                        let textArea = summaryHolder.querySelector("textarea");
                        if (!textArea) return;
                        let newText = textArea.value;
                        if (!newText) return;
                        console.log(newText);

                        // server side update
                        let id = submitButton.closest("div[data-job-id]").dataset.jobId;
                        let url = '/jobs/summary/' + id;
                        await postData(url, {summary: newText});

                        // remove everything
                        if (textArea) textArea.remove();
                        let buttonDiv = summaryHolder.querySelector(".summary-button-holder");
                        if (buttonDiv) buttonDiv.remove();

                        let pre = document.createElement("pre");
                        pre.appendChild(document.createTextNode(newText));
                        summaryHolder.appendChild(pre);


                    } catch (err) {
                        console.log(err);
                    }
                };

                let cancelButton = document.createElement("button");
                cancelButton.classList.add("btn-link");
                cancelButton.appendChild(document.createTextNode("cancel"));
                cancelButton.onclick = () => {

                    let summaryHolder = cancelButton.closest(".summary");

                    let tArea = summaryHolder.querySelector("textarea");
                    if (tArea) tArea.remove();

                    let buttonDiv = summaryHolder.querySelector(".summary-button-holder");
                    if (buttonDiv) buttonDiv.remove();

                    let pre = document.createElement("pre");
                    pre.appendChild(document.createTextNode(summary));
                    summaryHolder.appendChild(pre);
                };

                pre.remove();
                preParent.appendChild(tArea);

                let buttonDiv = document.createElement("div");
                buttonDiv.classList.add("summary-button-holder")
                buttonDiv.appendChild(submitButton);
                buttonDiv.appendChild(cancelButton);

                preParent.appendChild(buttonDiv);

                tArea.focus();
                tArea.dispatchEvent(new Event("input"));

            } catch (err) {
                console.log(err);
            }
        }
    });

    // blacklist a company
    document.querySelectorAll("button[data-company]").forEach(bl => {
        bl.onclick = async () => {
            try {
                let company = bl.dataset.company;
                if (!company) return;

                let url = '/jobs/blacklist/' + company;
                await postData(url, {});

                location.reload();

            } catch (err) {
                console.log(err);
            }
        }
    });

    // remove a blacklisted company
    document.querySelectorAll("button[data-remove-blacklisted-company]").forEach(rblc => {
        rblc.onclick = async () => {
            try {
                let company = rblc.value;
                if (!company) return;

                let url = '/jobs/blacklist/' + company;
                await deleteData(url);

                location.reload();
            } catch (err) {
                console.log(err);
            }
        };
    });

    // click the include button for the title filter search pane
    document.querySelector("input[name='blockTitleChecked']").onclick = async (e) => {
        try {

            let checked = e.target.checked;
            if (checked === undefined || checked === null) return;

            let url = '/jobs/titlefilter/' + checked;
            await postData(url, {});

            location.reload();
        } catch (err) {
            console.log(err);
        }

    };

    // select one of the label filter inputs
    document.querySelectorAll("input[data-label-filter]").forEach(dlf => {
        dlf.onclick = async () => {
            try {
                let checked = dlf.checked;
                if (checked === undefined || checked === null) return;

                let filter = dlf.dataset.labelFilter;
                if (!filter) return;

                let url = '/jobs/labelfilter/' + filter + "/" + checked;
                await postData(url, {});
                location.reload();
            } catch (err) {
                console.log(err);
            }
        }
    });

    async function postData(url = '', data = {}) {
        // Default options are marked with *
        const response = await fetch(url, {
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

    async function deleteData(url = '') {
        const response = await fetch(url, {
            method: 'DELETE', // *GET, POST, PUT, DELETE, etc.
            mode: 'cors', // no-cors, *cors, same-origin
            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
            credentials: 'same-origin', // include, *same-origin, omit
            headers: {
                // 'Content-Type': 'application/json'
                // 'Content-Type': 'application/x-www-form-urlencoded',
            },
            redirect: 'follow', // manual, *follow, error
            referrer: 'no-referrer', // no-referrer, *client
            // body: JSON.stringify(data) // body data type must match "Content-Type" header
        });
        if (!response.ok) {
            throw Error(response.statusText);
        }
        return response;
    }

};
