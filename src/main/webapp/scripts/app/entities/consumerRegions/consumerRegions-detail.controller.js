'use strict';

angular.module('reachoutApp')
    .controller('ConsumerRegionsDetailController', function ($scope, $rootScope, $stateParams, entity, ConsumerRegions) {
        $scope.consumerRegions = entity;
        $scope.load = function (id) {
            ConsumerRegions.get({id: id}, function(result) {
                $scope.consumerRegions = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:consumerRegionsUpdate', function(event, result) {
            $scope.consumerRegions = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
