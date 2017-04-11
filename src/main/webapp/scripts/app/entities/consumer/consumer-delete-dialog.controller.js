'use strict';

angular.module('reachoutApp')
	.controller('ConsumerDeleteController', function($scope, $uibModalInstance, entity, Consumer) {

        $scope.consumer = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Consumer.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
