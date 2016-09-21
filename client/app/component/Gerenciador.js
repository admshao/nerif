Ext.define('Nerif.component.Gerenciador', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.gerenciador',

    singleton: true,
    alternateClassName: ['Gerenciador'],

    data: {
        server: '',
        logDirectory: '',
        logFormat: []
    },

    formulas: {
        serverDescription: function (get) {
            var temp = '';

            temp += '{' + 'server' + ': ' + get('server') + '}';
            temp += '{' + 'logDirectory' + ': ' + get('logDirectory') + '}';
            temp += '{' + 'logFormat' + ': ' + get('logFormat') + '}';

            return temp;
        }
    },

    stores: {
        users: {
            fields: ['id', 'nome', 'email', 'telefone']
        },

        indicators: {
            fields: ['id', 'descricao', 'regras']
        },

        groups: {
            fields: ['id', 'descricao', 'users', 'indicators']
        }
    }
});