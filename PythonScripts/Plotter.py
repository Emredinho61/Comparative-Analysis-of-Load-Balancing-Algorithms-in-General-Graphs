import matplotlib.pyplot as plt
import re
import math


class Plotter:
    def __init__(self, files: list):
        self.files = files

    def MSE_per_Round(self, filepath: str):
        rounds = []
        MSEs = []
        try:
            file = open(filepath, 'rt')
            for one_line in file:
                regex = r"(Cycle No.: )([\d]*)|(MSE: )([\d|.|E\-\d]*)"
                matches = re.finditer(regex, one_line, re.MULTILINE)
                for match in matches:
                    # Round is second Element of Regex group
                    if match.groups()[1] is not None:
                        rounds.append(match.groups()[1])
                    # MSE is last Element (4th) of Regex group
                    if match.groups()[3] is not None:
                        MSEs.append(match.groups()[3])
            file.close()
            return [rounds, MSEs]
        except Exception as e:
            print(f"An exception occured while opening the file {e}")

    def plot_MSE(self, filepath_DAB: str, filepath_PPS: str):
        MSE_Rounds_DAB = self.MSE_per_Round(filepath_DAB)
        MSE_Rounds_PPS = self.MSE_per_Round(filepath_PPS)
        initial_MSE = float(MSE_Rounds_PPS[1][0])
        MSE_Rounds_DAB[1].insert(0, initial_MSE)
        print(MSE_Rounds_DAB)
        DAB_Plot = plt.plot([oneRound for oneRound in range(0, len(MSE_Rounds_DAB[0]) + 1, 1)],
                            [float(oneMSE) for oneMSE in MSE_Rounds_DAB[1][0:50:1]], label="DAB")
        plt.setp(DAB_Plot, color='r', linewidth=2.0)
        PPS_PLot = plt.plot([oneRound for oneRound in range(0, len(MSE_Rounds_PPS[0]) + 1, 1)],
                            [float(oneMSE) for oneMSE in MSE_Rounds_PPS[1][0:50:1]], label="PPS")
        plt.setp(PPS_PLot, color='g', linewidth=2.0)
        plt.xlabel("Rounds")
        plt.ylabel("Mean Squared Error")
        plt.title("Mean Squared Error per Round - Networksize 1000 Fully Connected Graph")
        leg = plt.legend(loc='upper center')
        # plt.show()
        plt.savefig("DAB_vs_PPS_fullyconnected_r50_n1000", transparent=None, dpi=700, format=None,
                    metadata=None, bbox_inches=None, pad_inches=0.1,
                    facecolor='auto', edgecolor='auto', backend=None,
                    )


if __name__ == "__main__":
    file1 = "../simulationResults/terminalOutput_DAB_1000.txt"
    file2 = "../simulationResults/terminalOutput_PPS_1000.txt"
    plotter_instance = Plotter(
        [file1, file2])

    plotter_instance.plot_MSE(file1, file2)
