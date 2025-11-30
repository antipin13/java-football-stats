class MatchPredictor {
    constructor() {
        this.apiBaseUrl = 'http://localhost:8080';
        this.homeTeamSelect = document.getElementById('homeTeam');
        this.awayTeamSelect = document.getElementById('awayTeam');
        this.predictBtn = document.getElementById('predictBtn');
        this.loadingElement = document.getElementById('loading');
        this.resultElement = document.getElementById('result');
        this.errorElement = document.getElementById('error');

        this.init();
    }

    init() {
        this.loadTeams();
        this.setupEventListeners();
    }

    async loadTeams() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/teams`);
            const teams = await response.json();

            this.populateTeamSelects(teams);
            this.initializeSelect2();
        } catch (error) {
            console.error('Ошибка загрузки команд:', error);
            this.populateTeamSelects(['Реал Мадрид', 'Барселона', 'Атлетико Мадрид', 'Севилья']);
        }
    }

    populateTeamSelects(teams) {
        const teamNames = teams.map(team => team.name);

        this.homeTeamSelect.innerHTML = '';
        this.awayTeamSelect.innerHTML = '';

        const emptyOption1 = new Option('Выберите команду', '');
        const emptyOption2 = new Option('Выберите команду', '');
        this.homeTeamSelect.appendChild(emptyOption1);
        this.awayTeamSelect.appendChild(emptyOption2);

        teamNames.forEach(team => {
            const option1 = new Option(team, team);
            const option2 = new Option(team, team);
            this.homeTeamSelect.appendChild(option1);
            this.awayTeamSelect.appendChild(option2);
        })
    }

    initializeSelect2() {
            $(this.homeTeamSelect).select2({
                placeholder: "Начните вводить название команды",
                //allowClear: true,
                language: "ru",
                width: '100%',
                minimumResultsForSearch: 1
            });

            $(this.awayTeamSelect).select2({
                placeholder: "Начните вводить название команды",
                allowClear: true,
                language: "ru",
                width: '100%',
                minimumResultsForSearch: 1
            });
        }

    setupEventListeners() {
        this.predictBtn.addEventListener('click', () => this.getPrediction());
    }

    async getPrediction() {
        const homeTeam = $(this.homeTeamSelect).val();
        const awayTeam = $(this.awayTeamSelect).val();
        const matchCount = document.getElementById('matchCount').value;

        if (!homeTeam || !awayTeam) {
            alert('Пожалуйста, выберите обе команды');
            return;
        }

        if (homeTeam === awayTeam) {
            alert('Команды не могут быть одинаковыми');
            return;
        }

        this.showLoading();
        this.hideResult();
        this.hideError();

        try {
            const prediction = await this.fetchPrediction(homeTeam, awayTeam, matchCount);
            this.displayPrediction(prediction);
        } catch (error) {
            console.error('Ошибка:', error);
            this.showError();
        } finally {
            this.hideLoading();
        }
    }

    async fetchPrediction(homeTeam, awayTeam, countLastMatches) {
        const url = `${this.apiBaseUrl}/match-predict?homeTeam=${encodeURIComponent(homeTeam)}&awayTeam=${encodeURIComponent(awayTeam)}&countLastMatches=${countLastMatches}`;

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    }

    displayPrediction(prediction) {
        document.getElementById('homeWinPercent').textContent = this.formatPercent(prediction.homeWinChance);
        document.getElementById('drawPercent').textContent = this.formatPercent(prediction.drawChance);
        document.getElementById('awayWinPercent').textContent = this.formatPercent(prediction.awayWinChance);

        if (prediction.homeExpectedGoals && prediction.awayExpectedGoals) {
            document.getElementById('expectedHomeGoals').textContent = prediction.homeExpectedGoals.toFixed(2);
            document.getElementById('expectedAwayGoals').textContent = prediction.awayExpectedGoals.toFixed(2);
        }

        if (prediction.scoreProbabilities) {
            this.displayScoreProbabilities(prediction.scoreProbabilities);
        }

        this.showResult();
    }

    displayScoreProbabilities(scoreProbabilities) {
        const scoreTable = document.getElementById('scoreTable');
        scoreTable.innerHTML = '';

        const sortedScores = Object.entries(scoreProbabilities)
            .sort(([,a], [,b]) => b - a)
            .slice(0, 12);

        sortedScores.forEach(([score, probability]) => {
            const scoreItem = document.createElement('div');
            scoreItem.className = 'score-item';
            scoreItem.innerHTML = `
                <div class="score">${score}</div>
                <div class="prob">${this.formatPercent(probability)}</div>
            `;
            scoreTable.appendChild(scoreItem);
        });
    }

    formatPercent(value) {
        return `${(value * 100).toFixed(1)}%`;
    }

    showLoading() {
        this.loadingElement.classList.remove('hidden');
    }

    hideLoading() {
        this.loadingElement.classList.add('hidden');
    }

    showResult() {
        this.resultElement.classList.remove('hidden');
    }

    hideResult() {
        this.resultElement.classList.add('hidden');
    }

    showError() {
        this.errorElement.classList.remove('hidden');
    }

    hideError() {
        this.errorElement.classList.add('hidden');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new MatchPredictor();
});