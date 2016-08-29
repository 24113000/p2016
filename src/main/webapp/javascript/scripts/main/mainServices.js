/**
 * Created by Sergii on 27.08.2016.
 */
angular.module('FileBrowserApp').factory('fileBrowserService',
    function($http, uriBuilder) {

        function getRootFolderChildren() {
           return $http.get(uriBuilder.getRootFolderURI())
               .error(function(data, status, headers, config) {
                   console.log('Status: ' + status);
                   console.log(data);
               });
        }

        function getFolderChildren() {

        }

        function createFolder() {

        }

        function deleteFile() {

        }

        function createFile() {

        }

        function renameFile() {

        }

        return {
            getRootFolderChildren: getRootFolderChildren,
            getFolderChildren: getFolderChildren,
            createFolder: createFolder,
            deleteFile: deleteFile,
            createFile: createFile,
            renameFile: renameFile
        }
    }
);

angular.module('FileBrowserApp').service('uriBuilder',
    function(contextNameConst){
        var uriBuilder = this;
        uriBuilder.restPrefix = contextNameConst + '/rest';

        uriBuilder.getRootFolderURI = function () {
            return uriBuilder.restPrefix + '/fileOperation/getFile/11';
        }
    }
);