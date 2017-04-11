(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('PakkaApplicationSettingsDeleteController',PakkaApplicationSettingsDeleteController);

    PakkaApplicationSettingsDeleteController.$inject = ['$uibModalInstance', 'entity', 'PakkaApplicationSettings'];

    function PakkaApplicationSettingsDeleteController($uibModalInstance, entity, PakkaApplicationSettings) {
        var vm = this;

        vm.pakkaApplicationSettings = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            PakkaApplicationSettings.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
