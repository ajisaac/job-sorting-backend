window.onload = () => {
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

    async function changeJobState(sb, state) {
        if (!sb || !state) throw Error("Missing job or state.");
        try {

            let id = sb.dataset.jobId;
            if (!id) return;

            let url = '/jobs/state/' + state + '/' + id;
            await postData(url, {});

            let compContent = sb.closest(".company-content");

            sb.closest("div[data-job-id='" + id + "']").remove();
            if (compContent.children.length <= 0) {
                sb.closest(".company").remove();
            }

        } catch (error) {
            console.log(error);
        }
    }

    async function changeMultipleJobState(ca, state) {
        if (!ca || !state) throw Error("Missing company or state.");
        try {

            // get all our job ids
            let jobs = Array.from(ca.closest(".company").querySelectorAll("div[data-job-id]"));
            console.log(jobs);

            let jobIds = jobs.map(j => {
                let id = j.dataset.jobId;
                if (id) {
                    return id;
                }
                return 0;
            });
            console.log(jobIds);

            let url = '/jobs/multiplestates/' + state;
            await postData(url, jobIds);

            ca.closest(".company").remove();

        } catch (error) {
            console.log(error);
        }
    }

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
};