(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('PakkaApplicationSettingsDialogController', PakkaApplicationSettingsDialogController);

    PakkaApplicationSettingsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'PakkaApplicationSettings'];

    function PakkaApplicationSettingsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, PakkaApplicationSettings) {
        var vm = this;

        vm.pakkaApplicationSettings = entity;
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
            if (vm.pakkaApplicationSettings.id !== null) {
                PakkaApplicationSettings.update(vm.pakkaApplicationSettings, onSaveSuccess, onSaveError);
            } else {
                PakkaApplicationSettings.save(vm.pakkaApplicationSettings, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('reachoutApp:pakkaApplicationSettingsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
