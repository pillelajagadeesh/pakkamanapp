(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('LocalitiesDialogController', LocalitiesDialogController);

    LocalitiesDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Localities'];

    function LocalitiesDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Localities) {
        var vm = this;

        vm.localities = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.localities.id !== null) {
                Localities.update(vm.localities, onSaveSuccess, onSaveError);
            } else {
                Localities.save(vm.localities, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('reachoutApp:localitiesUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
