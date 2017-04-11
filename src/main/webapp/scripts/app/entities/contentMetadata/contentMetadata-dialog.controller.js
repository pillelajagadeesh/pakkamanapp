'use strict';

angular.module('reachoutApp').controller('ContentMetadataDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'ContentMetadata',
        function($scope, $stateParams, $uibModalInstance, entity, ContentMetadata) {

        $scope.contentMetadata = entity;
        $scope.load = function(id) {
            ContentMetadata.get({id : id}, function(result) {
                $scope.contentMetadata = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:contentMetadataUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.contentMetadata.id != null) {
                ContentMetadata.update($scope.contentMetadata, onSaveSuccess, onSaveError);
            } else {
                ContentMetadata.save($scope.contentMetadata, onSaveSuccess, onSaveError);
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
