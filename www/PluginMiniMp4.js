
cordova.define("cordova/plugins/PluginMiniMp4", 
  function(require, exports, module) {
    var exec = require("cordova/exec");
    var PluginMiniMp4 = function() {};
	  PluginMiniMp4.prototype.showmsg = function(inputMsg,successCallback, errorCallback) {
        
        if (typeof errorCallback != "function")  {
            console.log("error");
            return
        }
    
        if (typeof successCallback != "function") {
            console.log("error");
            return
        }
        exec(successCallback, errorCallback, 'PluginMiniMp4', 'showmsg', [{"limit":inputMsg}]);
    };
	
    var PluginMiniMp4 = new PluginMiniMp4();
    module.exports = PluginMiniMp4;

});

  
if(!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.PluginMiniMp4) {
    window.plugins.PluginMiniMp4 = cordova.require("cordova/plugins/PluginMiniMp4");
}