'use strict';

angular.module('reachoutApp')
	.controller('ConsumerRegionsDeleteController', function($scope, $uibModalInstance, entity, ConsumerRegions) {

        $scope.consumerRegions = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            ConsumerRegions.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
