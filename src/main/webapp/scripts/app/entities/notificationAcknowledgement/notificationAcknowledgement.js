'use strict';

angular.module('reachoutApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('notificationAcknowledgement', {
                parent: 'entity',
                url: '/notificationAcknowledgements',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.notificationAcknowledgement.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/notificationAcknowledgement/notificationAcknowledgements.html',
                        controller: 'NotificationAcknowledgementController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('notificationAcknowledgement');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('notificationAcknowledgement.detail', {
                parent: 'entity',
                url: '/notificationAcknowledgement/{id}',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.notificationAcknowledgement.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/notificationAcknowledgement/notificationAcknowledgement-detail.html',
                        controller: 'NotificationAcknowledgementDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('notificationAcknowledgement');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'NotificationAcknowledgement', function($stateParams, NotificationAcknowledgement) {
                        return NotificationAcknowledgement.get({id : $stateParams.id});
                    }]
                }
            })
            .state('notificationAcknowledgement.new', {
                parent: 'notificationAcknowledgement',
                url: '/new',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notificationAcknowledgement/notificationAcknowledgement-dialog.html',
                        controller: 'NotificationAcknowledgementDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    read: null,
                                    consumerId: null,
                                    notificationId: null,
                                    delivered: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('notificationAcknowledgement', null, { reload: true });
                    }, function() {
                        $state.go('notificationAcknowledgement');
                    })
                }]
            })
            .state('notificationAcknowledgement.edit', {
                parent: 'notificationAcknowledgement',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notificationAcknowledgement/notificationAcknowledgement-dialog.html',
                        controller: 'NotificationAcknowledgementDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['NotificationAcknowledgement', function(NotificationAcknowledgement) {
                                return NotificationAcknowledgement.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('notificationAcknowledgement', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('notificationAcknowledgement.delete', {
                parent: 'notificationAcknowledgement',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/notificationAcknowledgement/notificationAcknowledgement-delete-dialog.html',
                        controller: 'NotificationAcknowledgementDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['NotificationAcknowledgement', function(NotificationAcknowledgement) {
                                return NotificationAcknowledgement.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('notificationAcknowledgement', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
