'use strict';

angular.module('reachoutApp')
	.controller('ConsumerFeedbackDeleteController', function($scope, $uibModalInstance, entity, ConsumerFeedback) {

        $scope.consumerFeedback = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            ConsumerFeedback.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
