'use strict';

angular.module('reachoutApp').controller('ProviderDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Provider',
        function($scope, $stateParams, $uibModalInstance, entity, Provider) {

        $scope.provider = entity;
        $scope.load = function(id) {
        	Provider.get({id : id}, function(result) {
                $scope.provider = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:providerUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.provider.id != null) {
            	Provider.update($scope.provider, onSaveSuccess, onSaveError);
            } else {
            	Provider.save($scope.provider, onSaveSuccess, onSaveError);
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
