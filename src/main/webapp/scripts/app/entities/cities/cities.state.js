(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('cities', {
            parent: 'entity',
            url: '/cities',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reachoutApp.cities.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/entities/cities/cities.html',
                    controller: 'CitiesController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('cities');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('cities-detail', {
            parent: 'entity',
            url: '/cities/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reachoutApp.cities.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/entities/cities/cities-detail.html',
                    controller: 'CitiesDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('cities');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Cities', function($stateParams, Cities) {
                    return Cities.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('cities.new', {
            parent: 'cities',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'scripts/app/entities/cities/cities-dialog.html',
                    controller: 'CitiesDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                cityname: null,
                                citylocation: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('cities', null, { reload: true });
                }, function() {
                    $state.go('cities');
                });
            }]
        })
        .state('cities.edit', {
            parent: 'cities',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'scripts/app/entities/cities/cities-dialog.html',
                    controller: 'CitiesDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Cities', function(Cities) {
                            return Cities.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('cities', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('cities.delete', {
            parent: 'cities',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'scripts/app/entities/cities/cities-delete-dialog.html',
                    controller: 'CitiesDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Cities', function(Cities) {
                            return Cities.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('cities', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
