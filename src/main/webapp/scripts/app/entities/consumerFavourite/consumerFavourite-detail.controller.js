'use strict';

angular.module('reachoutApp')
    .controller('ConsumerFavouriteDetailController', function ($scope, $rootScope, $stateParams, entity, ConsumerFavourite) {
        $scope.consumerFavourite = entity;
        $scope.load = function (id) {
            ConsumerFavourite.get({id: id}, function(result) {
                $scope.consumerFavourite = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:consumerFavouriteUpdate', function(event, result) {
            $scope.consumerFavourite = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
