'use strict';

angular.module('reachoutApp')
    .controller('NotificationAcknowledgementDetailController', function ($scope, $rootScope, $stateParams, entity, NotificationAcknowledgement) {
        $scope.notificationAcknowledgement = entity;
        $scope.load = function (id) {
            NotificationAcknowledgement.get({id: id}, function(result) {
                $scope.notificationAcknowledgement = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:notificationAcknowledgementUpdate', function(event, result) {
            $scope.notificationAcknowledgement = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
