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
            with open(filepath, 'rt') as file:
                for one_line in file:
                    regex = r"(Cycle No.: )([\d]*)|(MSE: )([\d|.|E\-\d]*)|(Config: )([\w| ]*)"
                    matches = re.finditer(regex, one_line, re.MULTILINE)
                    for match in matches:
                        if match.groups()[1] is not None:
                            rounds.append(match.groups()[1])
                        if match.groups()[3] is not None:
                            MSEs.append(match.groups()[3])
                        if match.groups()[5] is not None:
                            network_information = match.groups()[5].split(" ")
                            self.networksize = network_information[0]
                            self.networktype = " ".join(network_information[1:])
            return [rounds, MSEs]
        except Exception as e:
            print(f"An exception occurred while opening the file {e}")

    def abbreviate_networktype(self):
        abbreviaion = [oneWord[0] for oneWord in self.networktype.split(" ")]
        abbreviaion_string = "".join(abbreviaion)
        return abbreviaion_string

    def plot_MSE(self, filepath_DAB: str, filepath_PPS: str, linewidth=1.0):
        MSE_Rounds_DAB = self.MSE_per_Round(filepath_DAB)
        MSE_Rounds_PPS = self.MSE_per_Round(filepath_PPS)
        initial_MSE = float(MSE_Rounds_PPS[1][0])
        MSE_Rounds_DAB[1].insert(0, initial_MSE)

        plt.figure(figsize=(10, 6))
        rounds_range_DAB = range(0, len(MSE_Rounds_DAB[0]) + 1, 1)
        rounds_range_PPS = range(0, len(MSE_Rounds_PPS[0]) + 1, 1)

        DAB_Plot = plt.plot(rounds_range_DAB,
                            [float(oneMSE) for oneMSE in MSE_Rounds_DAB[1][0:50:1]],
                            label="DAB", color='r', linewidth=linewidth, marker='o', markersize=4)

        PPS_Plot = plt.plot(rounds_range_PPS,
                            [float(oneMSE) for oneMSE in MSE_Rounds_PPS[1][0:50:1]],
                            label="PPS", color='g', linewidth=linewidth, marker='x', markersize=4)

        plt.yscale('log')
        plt.xlabel("Rounds", fontsize=12)
        plt.ylabel("Mean Squared Error", fontsize=12)
        plt.title(f"Network Size {self.networksize} {self.networktype}", fontsize=14)
        plt.grid(True, which='both', linestyle='--', linewidth=0.5)
        plt.legend(loc='upper right', fontsize=10)
        plt.tight_layout()

        abbreviation = self.abbreviate_networktype()
        plt.savefig(f"Plots/DAB_vs_PPS_{abbreviation}_r50_n{self.networksize}.png",
                    transparent=False, dpi=700, bbox_inches='tight', pad_inches=0.1)


if __name__ == "__main__":
    file1 = "./Plots/StarGraph/terminalOutput_DAB_10000.txt"
    file2 = "./Plots/StarGraph/terminalOutput_PPS_10000.txt"
    plotter_instance = Plotter([file1, file2])
    plotter_instance.plot_MSE(file1, file2, linewidth=0.5)
