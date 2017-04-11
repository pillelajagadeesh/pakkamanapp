'use strict';

angular.module('reachoutApp')
    .controller('ConsumerDetailController', function ($scope, $rootScope, $stateParams, entity, Consumer) {
        $scope.consumer = entity;
        $scope.load = function (id) {
            Consumer.get({id: id}, function(result) {
                $scope.consumer = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:consumerUpdate', function(event, result) {
            $scope.consumer = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
