window.onload = () => {
    let numJobsSpan = document.querySelector("span[data-num-jobs]");
    let numCompaniesSpan = document.querySelector("span[data-num-companies]");

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

    document.querySelectorAll("button[data-job-action]").forEach(sb => {
        let actionType = sb.dataset.jobAction;
        sb.onclick = async () => {
            await changeJobState(sb, actionType);
        }
    });

    document.querySelectorAll("button[data-company-action]").forEach(ca => {
        let actionType = ca.dataset.companyAction;
        ca.onclick = async () => {
            await changeMultipleJobState(ca, actionType);
        }
    });

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

    function lowerJobCount(amount = 0) {
        if (!amount || isNaN(amount)) return;
        let num = parseInt(numJobsSpan.textContent);
        if (!num || isNaN(num)) return;

        num = num - amount;
        numJobsSpan.textContent = num.toString();
    }

    function lowerCompanyCount(amount = 0) {
        if (!amount || isNaN(amount)) return;
        let num = parseInt(numCompaniesSpan.textContent);
        if (!num || isNaN(num)) return;

        num = num - amount;
        numCompaniesSpan.textContent = num.toString();
    }

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

    // post to the server to change the state of one job
    async function changeJobState(sb, state) {
        if (!sb || !state) throw Error("Missing job or state.");
        try {

            let id = sb.dataset.jobId;
            if (!id) return;

            let url = '/jobs/state/' + state + '/' + id;
            await postData(url, {});

            let compContent = sb.closest(".company-content");
            let company = sb.closest(".company");

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

            lowerJobCount(jobs.length);
            lowerCompanyCount(1);

            ca.closest(".company").remove();

        } catch (error) {
            console.log(error);
        }
    }

};