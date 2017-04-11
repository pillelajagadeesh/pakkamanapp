'use strict';

angular.module('reachoutApp').controller('PrivateMessageDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'PrivateMessage',
        function($scope, $stateParams, $uibModalInstance, entity, PrivateMessage) {

        $scope.privateMessage = entity;
        $scope.load = function(id) {
            PrivateMessage.get({id : id}, function(result) {
                $scope.privateMessage = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:privateMessageUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.privateMessage.id != null) {
                PrivateMessage.update($scope.privateMessage, onSaveSuccess, onSaveError);
            } else {
                PrivateMessage.save($scope.privateMessage, onSaveSuccess, onSaveError);
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
}]);
