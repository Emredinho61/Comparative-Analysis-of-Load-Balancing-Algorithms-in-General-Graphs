import matplotlib.pyplot as plt
import re
import math


class Plotter:
    def __init__(self, files: list):
        self.files = files
        self.networktype = ""
        self.networksize = 0

    def MSE_per_Round(self, filepath: str):
        rounds = []
        MSEs = []
        try:
            file = open(filepath, 'rt')
            for one_line in file:
                regex = r"(Cycle No.: )([\d]*)|(MSE: )([\d|.|E\-\d]*)|(Config: )([\w| ]*)"
                matches = re.finditer(regex, one_line, re.MULTILINE)
                for match in matches:
                    # Round is second Element of Regex group
                    if match.groups()[1] is not None:
                        rounds.append(match.groups()[1])
                    # MSE is 4th of Regex group
                    if match.groups()[3] is not None:
                        MSEs.append(match.groups()[3])
                    if match.groups()[5] is not None:
                        network_information = match.groups()[5].split(" ")
                        self.networksize = network_information[0]
                        self.networktype = " ".join(network_information[1:])
            file.close()
            return [rounds, MSEs]
        except Exception as e:
            print(f"An exception occured while opening the file {e}")

    def abbreviate_networktype(self):
        print(self.networktype)
        abbreviaion = [oneWord[0] for oneWord in self.networktype.split(" ")]
        abbreviaion_string = "".join(abbreviaion)
        return abbreviaion_string

    def plot_MSE(self, filepath_DAB: str, filepath_PPS: str, linewidth=1.0):
        MSE_Rounds_DAB = self.MSE_per_Round(filepath_DAB)
        MSE_Rounds_PPS = self.MSE_per_Round(filepath_PPS)
        initial_MSE = float(MSE_Rounds_PPS[1][0])
        MSE_Rounds_DAB[1].insert(0, initial_MSE)
        print(MSE_Rounds_DAB)
        DAB_Plot = plt.plot([oneRound for oneRound in range(0, len(MSE_Rounds_DAB[0]) + 1, 1)],
                            [float(oneMSE) for oneMSE in MSE_Rounds_DAB[1][0:50:1]], label="DAB")
        plt.setp(DAB_Plot, color='r', linewidth=linewidth)
        PPS_PLot = plt.plot([oneRound for oneRound in range(0, len(MSE_Rounds_PPS[0]) + 1, 1)],
                            [float(oneMSE) for oneMSE in MSE_Rounds_PPS[1][0:50:1]], label="PPS")
        plt.setp(PPS_PLot, color='g', linewidth=linewidth)
        plt.xlabel("Rounds")
        plt.ylabel("Mean Squared Error")
        plt.title(f"Mean Squared Error per Round - Networksize {self.networksize} {self.networktype}")
        leg = plt.legend(loc='upper center')
        # plt.show()
        abbreviation = self.abbreviate_networktype()
        plt.savefig(f"DAB_vs_PPS_{abbreviation}_r50_n{self.networksize}", transparent=None, dpi=700, format=None,
                    metadata=None, bbox_inches=None, pad_inches=0.1,
                    facecolor='auto', edgecolor='auto', backend=None,
                    )


if __name__ == "__main__":
    file1 = "../simulationResults/terminalOutput_DAB_1000.txt"
    file2 = "../simulationResults/terminalOutput_PPS_1000.txt"
    plotter_instance = Plotter(
        [file1, file2])

    plotter_instance.plot_MSE(file1, file2, linewidth=0.5)
