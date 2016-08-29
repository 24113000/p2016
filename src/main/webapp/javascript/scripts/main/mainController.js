/**
 * Created by Sergii on 24.08.2016.
 */
angular.module('FileBrowserApp', []).controller('fileBrowserController', function ($scope, fileBrowserService) {
    var fileCtrl = this;

    fileCtrl.getRootFolderChildren = function () {
        fileBrowserService.getRootFolderChildren().then(function(result){
            console.log(result);
        });
    };

    fileCtrl.getFolderChildren = function() {

    };

    fileCtrl.createFolder = function() {

    };

    fileCtrl.deleteFile = function() {

    };

    fileCtrl.createFile = function() {

    };

    fileCtrl.renameFile = function() {

    };
});