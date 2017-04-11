'use strict';

angular.module('reachoutApp').controller('ConsumerDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Consumer',
        function($scope, $stateParams, $uibModalInstance, entity, Consumer) {

        $scope.consumer = entity;
        $scope.load = function(id) {
            Consumer.get({id : id}, function(result) {
                $scope.consumer = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:consumerUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.consumer.id != null) {
                Consumer.update($scope.consumer, onSaveSuccess, onSaveError);
            } else {
                Consumer.save($scope.consumer, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForCreated = {};

        $scope.datePickerForCreated.status = {
            opened: false
        };

        $scope.datePickerForCreatedOpen = function($event) {
            $scope.datePickerForCreated.status.opened = true;
        };
        $scope.datePickerForUpdated = {};

        $scope.datePickerForUpdated.status = {
            opened: false
        };

        $scope.datePickerForUpdatedOpen = function($event) {
            $scope.datePickerForUpdated.status.opened = true;
        };
}]);
