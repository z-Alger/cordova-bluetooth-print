var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'printer', 'coolMethod', [arg0]);
};
var mPrinter = {
        connected:function (success, error) {
            exec(success, error, 'PrinterPlugin', 'connected', []);
        };
        find:function (success, error) {
            exec(success, error, 'PrinterPlugin', 'find', []);
        };
        connect:function (arg0, success, error) {
            exec(success, error, 'PrinterPlugin', 'connect', [arg0]);
        };
        print:function (arg0, success, error) {
            exec(success, error, 'PrinterPlugin', 'print', [arg0]);

        };
        close:function (success, error) {
            exec(success, error, 'PrinterPlugin', 'close', []);

        }
}

module.exports = mPrinter;