'use strict';

angular.module('reachoutApp')
    .controller('ContentMetadataDetailController', function ($scope, $rootScope, $stateParams, entity, ContentMetadata) {
        $scope.contentMetadata = entity;
        $scope.load = function (id) {
        	ContentMetadata.get({id: id}, function(result) {
                $scope.contentMetadata = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:contentMetadataUpdate', function(event, result) {
            $scope.contentMetadata = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
