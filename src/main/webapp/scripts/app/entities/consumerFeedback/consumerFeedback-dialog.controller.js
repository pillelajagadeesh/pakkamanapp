'use strict';

angular.module('reachoutApp').controller('ConsumerFeedbackDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'ConsumerFeedback',
        function($scope, $stateParams, $uibModalInstance, entity, ConsumerFeedback) {

        $scope.consumerFeedback = entity;
        $scope.load = function(id) {
            ConsumerFeedback.get({id : id}, function(result) {
                $scope.consumerFeedback = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:consumerFeedbackUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.consumerFeedback.id != null) {
                ConsumerFeedback.update($scope.consumerFeedback, onSaveSuccess, onSaveError);
            } else {
                ConsumerFeedback.save($scope.consumerFeedback, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
