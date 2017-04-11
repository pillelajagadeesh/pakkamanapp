'use strict';

angular.module('reachoutApp')
    .controller('NotificationDetailController', function ($scope, $rootScope, $stateParams, entity, Notification) {
        $scope.notification = entity;
        $scope.myInterval = 3000;
        $scope.load = function (id) {
            Notification.get({id: id}, function(result) {
                $scope.notification = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:notificationUpdate', function(event, result) {
            $scope.notification = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
