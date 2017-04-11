'use strict';

angular.module('reachoutApp')
    .controller('RegionDetailController', function ($scope, $rootScope, $stateParams, entity, Region) {
        $scope.region = entity;
        $scope.load = function (id) {
            Region.get({id: id}, function(result) {
                $scope.region = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:regionUpdate', function(event, result) {
            $scope.region = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
