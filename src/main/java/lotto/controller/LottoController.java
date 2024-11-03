package lotto.controller;

import java.util.List;
import lotto.model.domain.Player;
import lotto.model.service.LottoService;
import lotto.model.service.WinningNumbersService;
import lotto.util.Validator;
import lotto.view.InputView;
import lotto.view.OutputView;

public class LottoController {
    OutputView outputView;
    InputView inputView;
    LottoService lottoService;
    WinningNumbersService winningNumbersService;
    private Player player;
    private int purchaseAmount;
    private List<Integer> winningNumbers;

    public LottoController(InputView inputView,
                           OutputView outputView,
                           LottoService lottoService,
                           WinningNumbersService winningNumbersService
    ) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.lottoService = lottoService;
        this.winningNumbersService = winningNumbersService;
    }

    public void run() {
        setupLotto();
        generateLottos();
        generateBonusNumber();
        resultLotto();
    }

    private void setupLotto() {
        purchaseAmount = 0;

        while (true) {
            try {
                outputView.purchaseLottoAmountMesssage();
                purchaseAmount = inputView.getPurchaseAmount();
                Validator.validateMoneyUnit(purchaseAmount);

                break;
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }

        player = new Player(purchaseAmount);
        lottoService.generateLottoTickets(player);
        processPurchase();
    }

    private void generateLottos() {
        outputView.printLottoNumbers(player.getLottoNumbers());

        while (true) {
            try {
                outputView.enterWinningNumbers();
                winningNumbersService.inputWinningNumbers();
                winningNumbers = winningNumbersService.getWinningNumbers().getNumbers();

                Validator.validateDuplicateNumber(winningNumbers);
                Validator.validateNumberCount(winningNumbers);
                for (int number : winningNumbers) {
                    Validator.validateRange(number);
                }

                break;
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
        player.setWinningNumbers(winningNumbersService.getWinningNumbers());
    }

    private void generateBonusNumber() {
        while (true) {
            try {
                outputView.enterBonusNumber();
                int bonusNumber = winningNumbersService.inputBonusNumber();

                Validator.validateRange(bonusNumber);
                Validator.validateDuplicateNumber(winningNumbers, bonusNumber);
                break;
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void resultLotto() {
        outputView.WinningStatistics();
        outputView.matchWinningCount(player.checkWinning());
        outputView.promptTotalReturnRate(player.getRateOfReturn(player.getWinningMoney()));
    }

    private void processPurchase() {
        int ticketCount = lottoService.calculateTicketCount(player.getPurchaseAmount());
        outputView.purchaseLottoCountMessage(ticketCount);
    }
}
