(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('LocalitiesDeleteController',LocalitiesDeleteController);

    LocalitiesDeleteController.$inject = ['$uibModalInstance', 'entity', 'Localities'];

    function LocalitiesDeleteController($uibModalInstance, entity, Localities) {
        var vm = this;

        vm.localities = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Localities.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
