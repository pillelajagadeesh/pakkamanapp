'use strict';

angular.module('reachoutApp')
	.controller('ConsumerFavouriteDeleteController', function($scope, $uibModalInstance, entity, ConsumerFavourite) {

        $scope.consumerFavourite = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            ConsumerFavourite.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
