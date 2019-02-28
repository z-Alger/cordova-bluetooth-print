# Cordova-Plugin-Bluetooth-Printer
A cordova plugin for bluetooth printer for android platform, which support text printing and POS printing.

##Support

- Image Printing (todo)

##Install
Using the Cordova CLI and NPM, run:

```
cordova plugin add https://github.com/z-Alger/cordova-bluetooth-print.git
```



##Usage
Get list of paired bluetooth connected

```
mPrinter.connected(function(data){
        console.log("Success");
        console.log(data); //data is connected data array
    },function(err){
        console.log("Error");
        console.log(err);
    })
```


find printer

```
mPrinter.find(function(data){
	console.log("Success");
	console.log(data)
},function(err){
	console.log("Error");
	console.log(err)
})
```
Connect printer

```
mPrinter.connect("PrinterName",function(data){
	console.log("Success");
	console.log(data)
},function(err){
	console.log("Error");
	console.log(err)
})
```

Disconnect printer

```
mPrinter.close(function(data){
	console.log("Success");
	console.log(data)
},function(err){
	console.log("Error");
	console.log(err)
})
```



Print image

```
mPrinter.print("Image Base64 String", function(data){
    console.log("Success");
    console.log(data)
},function(err){
    console.log("Error");
    console.log(err)
})
```