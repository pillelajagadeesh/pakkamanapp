'use strict';

angular.module('reachoutApp').controller('ConsumerFavouriteDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'ConsumerFavourite',
        function($scope, $stateParams, $uibModalInstance, entity, ConsumerFavourite) {

        $scope.consumerFavourite = entity;
        $scope.load = function(id) {
            ConsumerFavourite.get({id : id}, function(result) {
                $scope.consumerFavourite = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:consumerFavouriteUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.consumerFavourite.id != null) {
                ConsumerFavourite.update($scope.consumerFavourite, onSaveSuccess, onSaveError);
            } else {
                ConsumerFavourite.save($scope.consumerFavourite, onSaveSuccess, onSaveError);
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
