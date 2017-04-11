'use strict';

angular.module('reachoutApp')
	.controller('ContentMetadataDeleteController', function($scope, $uibModalInstance, entity, Cloudinary) {

        $scope.cloudinary = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Cloudinary.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
