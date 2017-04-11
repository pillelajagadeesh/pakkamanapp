'use strict';

angular.module('reachoutApp')
    .controller('PrivateMessageDetailController', function ($scope, $rootScope, $stateParams, entity, PrivateMessage) {
        $scope.privateMessage = entity;
        $scope.load = function (id) {
            PrivateMessage.get({id: id}, function(result) {
                $scope.privateMessage = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:privateMessageUpdate', function(event, result) {
            $scope.privateMessage = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
